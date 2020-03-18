package com.misterymatch.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.FragmentSheet.LikeDislikeFragment;
import com.misterymatch.app.activity.SignInActivity;
import com.misterymatch.app.fragment.ChatFragment;
import com.misterymatch.app.fragment.FindMatchFragment;
import com.misterymatch.app.fragment.MatchingFragment;
import com.misterymatch.app.fragment.ProfileFragment;
import com.misterymatch.app.model.Likes;
import com.misterymatch.app.model.Profile;
import com.misterymatch.app.model.ReceiverResponseModel;
import com.misterymatch.app.model.RequestMatchModel;
import com.misterymatch.app.model.SenderResponseModel;
import com.misterymatch.app.model.User;
import com.misterymatch.app.twilio_video.IncomingVideoCallActivity;
import com.misterymatch.app.twilio_video.TwilloVideoActivity;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements LikeDislikeFragment.OnDismissListener {

    public static final String SEARCHING = "SEARCHING";
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String ACCEPTED = "ACCEPTED";
    private static final int DELAY_MILLIS = 10000;
    public static boolean isgetFindMatch = false;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.profile)
    CircleImageView profile;
    @BindView(R.id.home)
    TextView home;
    @BindView(R.id.chat)
    ImageView chat;
    boolean doubleBackToExitPressedOnce = false;
    private Handler handler;
    private Runnable runnable;
    private String matchStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (!SharedHelper.getBoolKey(this, "logged_in")) {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }

        getProfile();
        listenForMatches();
        transactFragment(FindMatchFragment.newInstance(), false);
    }

    @Override
    public void onResume() {
        super.onResume();
        initProfileViews();
    }

    @OnClick({R.id.profile, R.id.home, R.id.chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile:
                transactFragment(new ProfileFragment(), true);
                break;
            case R.id.home:
                if (!(getSupportFragmentManager().findFragmentById(R.id.container)
                        instanceof FindMatchFragment))
                    transactFragment(FindMatchFragment.newInstance(), false);
                break;
            case R.id.chat:
                transactFragment(new ChatFragment(), true);
                break;
            default:
                break;
        }
    }

    public void initProfileViews() {
        if (GlobalData.PROFILE != null) {
            User user = GlobalData.PROFILE.getUser();
            if (user != null) {
                Glide.with(getApplicationContext())
                        .load(user.getPicture())
                        .apply(new RequestOptions().placeholder(R.drawable.user)
                                .error(R.drawable.user).fitCenter().dontAnimate())
                        .into(profile);
            }
        } else {
            getProfile();
        }
    }

    private void getProfile() {
        HashMap<String, Object> map = new HashMap<>();
        if (!SharedHelper.getKey(this, "device_token").isEmpty()) {
            map.put("device_token", SharedHelper.getKey(this, "device_token"));
        }
        if (!SharedHelper.getKey(this, "device_id").isEmpty()) {
            map.put("device_id", SharedHelper.getKey(this, "device_id"));
        }
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Profile> call = GlobalData.api.getProfile(accessToken, map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call,
                                   @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    GlobalData.PROFILE = response.body();
                    Log.d(TAG, "getProfile HOME" + GlobalData.PROFILE.toString());
                    SharedHelper.putKey(getApplicationContext(), "user_id",
                            String.valueOf(GlobalData.PROFILE.getUser().getId()));
                    initProfileViews();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
            }
        });
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    private void listenForMatches() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                requestMatches();
                handler.postDelayed(this, DELAY_MILLIS);
            }
        };
        handler.post(runnable);
    }

    private void transactFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void transactDialogFragment(DialogFragment fragment) {
        if (getSupportFragmentManager()
                .findFragmentByTag(fragment.getClass().getSimpleName()) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
    }

    private void removeDialogFragment() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(MatchingFragment.class.getSimpleName());
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    private void showLikeDislikeFragment(Integer requestId, Integer participantId) {
        LikeDislikeFragment fragment = LikeDislikeFragment.newInstance(requestId, participantId);
        fragment.show(getSupportFragmentManager(), LikeDislikeFragment.class.getSimpleName());
    }

    private void dismissLikeDislikeFragment() {
        BottomSheetDialogFragment fragment = (BottomSheetDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(LikeDislikeFragment.class.getSimpleName());
        if (fragment != null) {
            fragment.dismiss();
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    private void launchIncomingCallActivity(Integer requestId, String callerName) {
        Intent intent = new Intent(getApplicationContext(), IncomingVideoCallActivity.class);
        intent.putExtra("request_id", requestId.intValue());
        intent.putExtra("name", callerName);
        intent.putExtra("from_notification", false);
        startActivityForResult(intent, GlobalData.INCOMING_ACTIVITY_REQUEST_CODE);
    }

    private void launchVideoActivity(Integer requestId, Integer participantId, String videoToken,
                                     boolean isReceiver) {
        Intent intent = new Intent(getApplicationContext(), TwilloVideoActivity.class);
        intent.putExtra("request_id", requestId.intValue());
        intent.putExtra("participant_id", participantId.intValue());
        intent.putExtra("video_token", videoToken);
        intent.putExtra("is_receiver", isReceiver);
        startActivity(intent);
    }

    private void sendMatchCancelledBroadcasts() {
        Intent intent = new Intent();
        intent.setAction(GlobalData.ACTION_MATCH_CANCELLED);
        intent.putExtra("cancelled", true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void removePendingPosts() {
        handler.removeCallbacks(runnable);
    }

    private void processSenderRequest(List<SenderResponseModel> list) {
        if (!list.isEmpty()) {
            SenderResponseModel model = list.get(0);
            String currentStatus = model.getStatus();
            switch (currentStatus) {
                case SEARCHING:
                    if (TextUtils.isEmpty(matchStatus)
                            || !matchStatus.equalsIgnoreCase(currentStatus)) {
                        matchStatus = currentStatus;
                        transactDialogFragment(MatchingFragment.newInstance(model.getId()));
                    }
                    break;

                case ACCEPTED:
                    if (TextUtils.isEmpty(matchStatus)
                            || !matchStatus.equalsIgnoreCase(currentStatus)) {
                        matchStatus = currentStatus;
                        if (model.getIsVideoCompleted().compareTo(0) == 0
                                && !TextUtils.isEmpty(model.getSenderToken())) {
                            removeDialogFragment();
                            launchVideoActivity(model.getId(), model.getReceiverId(),
                                    model.getSenderToken(), false);
                        } else if (model.getSenderRated().compareTo(1) != 0) {
                            showLikeDislikeFragment(model.getId(), model.getReceiverId());
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void processReceiverRequest(List<ReceiverResponseModel> list) {
        if (!list.isEmpty()) {
            ReceiverResponseModel model = list.get(0);
            String currentStatus = model.getRequest().getStatus();
            switch (currentStatus) {
                case SEARCHING:
                    if (TextUtils.isEmpty(matchStatus)
                            || !matchStatus.equalsIgnoreCase(currentStatus)) {
                        matchStatus = currentStatus;
                        String callerName = model.getRequest().getSender().getFirstName() + " "
                                + model.getReceiver().getLastName();
                        launchIncomingCallActivity(model.getRequestId(), callerName);
                    }
                    break;

                case ACCEPTED:
                    if (TextUtils.isEmpty(matchStatus)
                            || !matchStatus.equalsIgnoreCase(currentStatus)) {
                        matchStatus = currentStatus;
                        if (model.getRequest().getIsVideoCompleted().compareTo(0) == 0
                                && !TextUtils.isEmpty(model.getRequest().getReceiverToken())) {
                            launchVideoActivity(model.getRequest().getId(),
                                    model.getRequest().getSenderId(),
                                    model.getRequest().getReceiverToken(), true);
                        } else if (model.getRequest().getReceiverRated().compareTo(1) != 0) {
                            showLikeDislikeFragment(model.getRequest().getId(),
                                    model.getRequest().getSenderId());
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void requestMatches() {
        String userToken = SharedHelper.getKey(this, "access_token");
        Call<RequestMatchModel> call = GlobalData.api.requestMatches(userToken);
        call.enqueue(new Callback<RequestMatchModel>() {
            @Override
            public void onResponse(@NonNull Call<RequestMatchModel> call,
                                   @NonNull Response<RequestMatchModel> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        String user = response.body().getUser();
                        if (!TextUtils.isEmpty(user)) {
                            switch (user) {
                                case "sender":
                                    processSenderRequest(response.body().getSenderResponse());
                                    break;

                                case "receiver":
                                    processReceiverRequest(response.body().getReceiverResponse());
                                    break;

                                default:
                                    break;
                            }
                        } else /*if (!TextUtils.isEmpty(matchStatus))*/ {
                            matchStatus = null;
                            sendMatchCancelledBroadcasts();
                        }
                    }
                } else if (response.code() == 401) {
                    removePendingPosts();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                }
            }

            @Override
            public void onFailure(Call<RequestMatchModel> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void updateMatchInterest(int requestId, int participantId, int interest) {
        CustomDialog customDialog = new CustomDialog(this);
        customDialog.show();
        String userToken = SharedHelper.getKey(this, "access_token");
        Call<Likes> call = GlobalData.api.updateMatchInterest(userToken, requestId, participantId,
                interest);
        call.enqueue(new Callback<Likes>() {
            @Override
            public void onResponse(@NonNull Call<Likes> call, @NonNull Response<Likes> response) {
                customDialog.dismiss();
                if (response.code() == 200 && response.body() != null) {
                    dismissLikeDislikeFragment();
                }
            }

            @Override
            public void onFailure(Call<Likes> call, Throwable t) {
                customDialog.dismiss();
                Log.e(TAG, t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDismiss(int requestId, int participantId, int interest) {
        updateMatchInterest(requestId, participantId, interest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GlobalData.INCOMING_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK
                && data != null) {
            if (data.getBooleanExtra("accepted", false)) {
                matchStatus = ACCEPTED;
                launchVideoActivity(data.getIntExtra("request_id", -1),
                        data.getIntExtra("participant_id", -1),
                        data.getStringExtra("video_token"), true);
            } else if (data.getBooleanExtra("cancelled", false)) {
                matchStatus = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.container) instanceof FindMatchFragment) {
            if (!doubleBackToExitPressedOnce) {
                doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getString(R.string.please_click_back_again_to_exit),
                        Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            removePendingPosts();
            super.onBackPressed();
        }
    }
}
