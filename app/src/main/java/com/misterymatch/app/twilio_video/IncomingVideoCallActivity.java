package com.misterymatch.app.twilio_video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.misterymatch.app.R;
import com.misterymatch.app.library.Log;
import com.misterymatch.app.model.AcceptMatchModel;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingVideoCallActivity extends AppCompatActivity {

    private static final String TAG = IncomingVideoCallActivity.class.getSimpleName();

    @BindView(R.id.lblName)
    TextView lblName;
    @BindView(R.id.imgEndCall)
    ImageView imgEndCall;
    @BindView(R.id.imgAcceptCall)
    ImageView imgAcceptCall;

    String chatPath;
    Ringtone ringtone;
    private CustomDialog customDialog;
    private Integer requestId;
    private MyBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_incoming_call);
        registerReceivers();
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        requestId = extras.getInt("request_id");
        String name = extras.getString("name");
        chatPath = extras.getString("chat_path");
        lblName.setText(name);
        playRingtone();
    }

    @OnClick({R.id.imgEndCall, R.id.imgAcceptCall})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgEndCall:
                cancelMatchRequest();
                break;
            case R.id.imgAcceptCall:
                acceptMatchRequest();
                break;
            default:
                break;
        }
    }

    private void playRingtone() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (notification != null) {
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringtone.play();
        }
    }

    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying())
            ringtone.stop();
    }

    private void acceptMatchRequest() {
        customDialog = new CustomDialog(this);
        customDialog.show();
        String userToken = SharedHelper.getKey(this, "access_token");
        Call<AcceptMatchModel> call = GlobalData.api.acceptMatchRequest(userToken, requestId);
        call.enqueue(new Callback<AcceptMatchModel>() {
            @Override
            public void onResponse(@NonNull Call<AcceptMatchModel> call,
                                   @NonNull Response<AcceptMatchModel> response) {
                customDialog.dismiss();
                if (response.code() == 200 && response.body() != null
                        && !TextUtils.isEmpty(response.body().getReceiverToken())) {
                    processMatchAcceptedResponse(response.body().getId(),
                            response.body().getSenderId(), response.body().getReceiverToken());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AcceptMatchModel> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Log.e(TAG, t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processMatchAcceptedResponse(Integer requestId, Integer participantId,
                                              String videoToken) {
        Intent intent = new Intent();
        intent.putExtra("request_id", requestId.intValue());
        intent.putExtra("participant_id", participantId.intValue());
        intent.putExtra("video_token", videoToken);
        if (getIntent().getBooleanExtra("from_notification", false)) {
            intent.setClass(getApplicationContext(), TwilloVideoActivity.class);
            intent.putExtra("is_receiver", true);
            startActivity(intent);
        } else {
            intent.putExtra("accepted", true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void cancelMatchRequest() {
        customDialog = new CustomDialog(this);
        customDialog.show();
        String userToken = SharedHelper.getKey(getBaseContext(), "access_token");
        Call<Object> call = GlobalData.api.cancelMatchRequest(userToken, requestId);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                customDialog.dismiss();
                if (response.code() == 200 && response.body() != null
                        && response.body() instanceof LinkedTreeMap) {
                    Toast.makeText(
                            getApplicationContext(),
                            String.valueOf(((LinkedTreeMap) response.body()).get("message")),
                            Toast.LENGTH_SHORT
                    ).show();
                    Intent intent = new Intent();
                    intent.putExtra("cancelled", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Log.e(TAG, t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerReceivers() {
        broadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(GlobalData.ACTION_MATCH_CANCELLED);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unRegisterReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceivers();
        stopRingtone();
    }

    @Override
    public void onBackPressed() {
        // Do nothing.
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("cancelled", false))
                finish();
        }
    }
}
