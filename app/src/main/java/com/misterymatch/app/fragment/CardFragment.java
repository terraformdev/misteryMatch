package com.misterymatch.app.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.R;
import com.misterymatch.app.activity.ProfileDetailedActivity;
import com.misterymatch.app.activity.SignInActivity;
import com.misterymatch.app.adapter.CardsAdapter;
import com.misterymatch.app.chat.ChatViewActivity;
import com.misterymatch.app.model.FindMatch;
import com.misterymatch.app.model.Like;
import com.misterymatch.app.model.Likes;
import com.misterymatch.app.model.User;
import com.misterymatch.app.model.UserLike;
import com.misterymatch.app.utils.CodeSnippet;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import in.arjsna.swipecardlib.SwipeCardView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.utils.GlobalData.api;

public class CardFragment extends Fragment {

    public static SwipeCardView swipeView;
    String TAG = "CardFragment";
    @BindView(R.id.status_layout)
    LinearLayout statusLayout;
    Unbinder unbinder;
    @BindView(R.id.no_matches)
    RelativeLayout mNoMatches;
    CardsAdapter cardsAdapter;
    List<User> list;
    @BindView(R.id.undo)
    ImageView undo;
    @BindView(R.id.dislike)
    ImageView dislike;
    @BindView(R.id.like)
    ImageView like;
    @BindView(R.id.super_like)
    ImageView superLike;
    @BindView(R.id.avatar)
    CircleImageView avatar;

    User currentCard = null;
    CodeSnippet codeSnippet;
    Dialog statusPopupDialog;
    Dialog premiumDialog;

    public CardFragment() {
    }

    public static CardFragment newInstance() {
        return new CardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        swipeView = view.findViewById(R.id.swipeView);
        unbinder = ButterKnife.bind(this, view);

        codeSnippet = new CodeSnippet(getActivity());

        statusPopupDialog = new Dialog(getActivity(), R.style.full_screen_dialog);
        statusPopupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        statusPopupDialog.getWindow().setBackgroundDrawable(null);
        statusPopupDialog.setContentView(R.layout.status_popup);
        WindowManager.LayoutParams lp = statusPopupDialog.getWindow().getAttributes();
        Window window = statusPopupDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        lp.gravity = Gravity.CENTER;


        list = new ArrayList<>();
        cardsAdapter = new CardsAdapter(getActivity(), list);
        swipeView.setAdapter(cardsAdapter);
        swipeView.setOnItemClickListener(new SwipeCardView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                GlobalData.USER = (User) dataObject;
                System.out.println(GlobalData.USER.getFirstName());
                startActivity(new Intent(getActivity(), ProfileDetailedActivity.class));
            }
        });

        swipeView.setFlingListener(new SwipeCardView.OnCardFlingListener() {
            @Override
            public void onCardExitLeft(Object dataObject) {
                Log.i(TAG, "Left Exit");
                currentCard = (User) dataObject;
                sendStatus(currentCard, "3");
            }

            @Override
            public void onCardExitRight(Object dataObject) {
                Log.i(TAG, "Right Exit");
                currentCard = (User) dataObject;
                sendStatus(currentCard, "1");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                Log.i(TAG, "CALLED Adater to be empty " + itemsInAdapter);
                //add more items to adapter and call notifydatasetchanged
                /*String[] urls = { "", "", "", ""};
                list.add(new Card("Six", 21, urls));
                list.add(new Card("Seven", 21, urls));
                list.add(new Card("Eight", 21, urls));
                cardsAdapter.notifyDataSetChanged();*/
                if(itemsInAdapter == 1) {
                    getFindMatch();
                }

                /*if(itemsInAdapter > 1){
                    getFindMatch();
                }*/

                if(itemsInAdapter == 0){
                    statusLayout.setVisibility(View.GONE);
                    mNoMatches.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = swipeView.getSelectedView();
                ImageView like = (ImageView) view.findViewById(R.id.like);
                ImageView dislike = (ImageView) view.findViewById(R.id.dislike);
                ImageView superlike = (ImageView) view.findViewById(R.id.superlike);

                if (scrollProgressPercent >= -0.01 && scrollProgressPercent <= 0.01 && scrollProgressPercent != 0.0) {
                    like.setImageAlpha(0);
                    dislike.setImageAlpha(0);
                    superlike.setImageAlpha(255);
                } else if (scrollProgressPercent >= 1.0) {
                    int transparency = (int) (scrollProgressPercent * 255);
                    superlike.setImageAlpha(0);
                    dislike.setImageAlpha(0);
                    like.setImageAlpha(transparency);
                } else if (scrollProgressPercent <= -1.0) {
                    int transparency = (int) (scrollProgressPercent * -255);
                    like.setImageAlpha(0);
                    superlike.setImageAlpha(0);
                    dislike.setImageAlpha(transparency);
                } else if (scrollProgressPercent == 0) {
                    like.setImageAlpha(0);
                    superlike.setImageAlpha(0);
                    dislike.setImageAlpha(0);
                }


                /*View view = swipeView.getSelectedView();

                ImageView like = view.findViewById(R.id.like);
                ImageView dislike = view.findViewById(R.id.dislike);
                ImageView status_popup = view.findViewById(R.id.status_popup);

                if (scrollProgressPercent > 0) {
                    int transparency = (int) (scrollProgressPercent * 255);
                    like.setImageAlpha(transparency);
                } else {
                    int transparency = (int) (scrollProgressPercent * -255);
                    dislike.setImageAlpha(transparency);
                }

                if (scrollProgressPercent == 0) {
                    like.setImageAlpha(0);
                    dislike.setImageAlpha(0);
                    status_popup.setImageAlpha(0);
                }*/

            }

            @Override
            public void onCardExitTop(Object dataObject) {
                Log.i(TAG, "Top Exit");
                currentCard = (User) dataObject;
                sendStatus(currentCard, "2");
            }

            @Override
            public void onCardExitBottom(Object dataObject) {
                currentCard = null;
                Log.i(TAG, "Bottom Exit");
            }
        });

        if(GlobalData.PROFILE != null){
            User user = GlobalData.PROFILE.getUser();
            Glide.with(getContext()).load(user.getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(avatar);
        }
        getFindMatch();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }

    private Point getCenterPointOfView(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int x = location[0] + view.getWidth() / 2;
        int y = location[1] + view.getHeight() / 2;
        return new Point(x, y);
    }

    private Point getPointOfView(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return new Point(location[0], location[1]);
    }

    private void getFindMatch() {
        System.out.println("CALLED getFindMatch");
        String accessToken = SharedHelper.getKey(getActivity(), "access_token");
        Call<FindMatch> call = api.findMatch(accessToken);
        call.enqueue(new Callback<FindMatch>() {
            @Override
            public void onResponse(@NonNull Call<FindMatch> call, @NonNull Response<FindMatch> response) {
                if (response.isSuccessful()) {
                    swipeView.setMaxVisible(1);
                    swipeView.setMinStackInAdapter(1);
                    System.out.println("CALLED SIZE " + response.body().getUser().size());
                    if (response.body().getUser().isEmpty()) {
                        swipeView.setVisibility(View.GONE);
                        statusLayout.setVisibility(View.GONE);
                        mNoMatches.setVisibility(View.VISIBLE);
                    } else {
                        if(response.body().getUser().size() <= 2){
                            return;
                        }
                        swipeView.setVisibility(View.VISIBLE);
                        list.addAll(response.body().getUser());
                        cardsAdapter.notifyDataSetChanged();
                        statusLayout.setVisibility(View.VISIBLE);
                        mNoMatches.setVisibility(View.GONE);
                    }
                } else {

                    if (response.code() == 429) {

                    } else {
                        statusLayout.setVisibility(View.GONE);
                        if (response.code() == 401) {
                            SharedHelper.putKey(getActivity(), "logged_in", false);
                            startActivity(new Intent(getActivity(), SignInActivity.class));
                            getActivity().finish();
                        } else if (response.code() == 500) {
                            Toast.makeText(getActivity(), "Internal Server Error", Toast.LENGTH_SHORT).show();
                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            //Toast.makeText(getActivity(), error.getError(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FindMatch> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "500 Internal Server Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick({R.id.undo, R.id.dislike, R.id.like, R.id.super_like})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.undo:
                if (currentCard != null) {
                    list.add(0, currentCard);
                    list.add(1, currentCard);
                    cardsAdapter.notifyDataSetChanged();
                    swipeView.throwBottom();
                }
                break;
            case R.id.dislike:
                swipeView.throwLeft();
                break;
            case R.id.like:
                swipeView.throwRight();
                break;
            case R.id.super_like:
                swipeView.throwTop();
                break;
        }
    }

    private void sendStatus(User currentCard, String status) {

        if (!codeSnippet.isConnectingToInternet()) {
            Toast.makeText(getContext(), getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        if (currentCard == null)
            return;

        Log.d(TAG, "Card id:" + currentCard.getId());

        final HashMap<String, String> map = new HashMap<>();
        map.put("like_id", String.valueOf(currentCard.getId()));
        map.put("status", status);

        Log.d(TAG, map.get("like_id") + "" + map.get("status"));

        System.out.println(map);
        String accessToken = SharedHelper.getKey(getContext(), "access_token");
        Call<Likes> call = api.sendStatus(accessToken, map);
        call.enqueue(new Callback<Likes>() {
            @Override
            public void onResponse(@NonNull Call<Likes> call, @NonNull Response<Likes> response) {

                if (response.isSuccessful()) {

                    if(response.body().getStatus().equalsIgnoreCase("SUPERLIKE_REACHED_LIMIT")){
                        premiumDialog = new PremiumDialog(getContext());

                        premiumDialog.show();
                    }else {
                        StatusPopUp(response.body());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Likes> call, @NonNull Throwable t) {
                //Toast.makeText(getContext(), "updateProfile Something Went Wrong" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void StatusPopUp(Likes Obj) {

        if (Obj.getLike() == null || Obj.getLike().getUserLike() == null || Obj.getStatus().isEmpty()) {
            return;
        }
        final Like like = Obj.getLike();

        TextView status = (TextView) statusPopupDialog.findViewById(R.id.status);
        TextView description = (TextView) statusPopupDialog.findViewById(R.id.description);
        CircleImageView avatar1 = (CircleImageView) statusPopupDialog.findViewById(R.id.avatar1);
        CircleImageView avatar2 = (CircleImageView) statusPopupDialog.findViewById(R.id.avatar2);
        ImageView close = (ImageView) statusPopupDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                statusPopupDialog.dismiss();
            }
        });
        Button sendMessage = (Button) statusPopupDialog.findViewById(R.id.send_a_message);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UserLike userLike = like.getUserLike();
                Intent intent = new Intent(getContext(), ChatViewActivity.class);
                intent.putExtra("sender", userLike.getId());
                intent.putExtra("sender_name", userLike.getFirstName());
                intent.putExtra("sender_avatar", userLike.getPicture());
                intent.putExtra("device_token", userLike.getDeviceToken());
                intent.putExtra("sender_mobile", userLike.getMobile());
                startActivity(intent);
                statusPopupDialog.dismiss();
            }
        });


        System.out.println("OBJ " + Obj.getStatus());
        switch (Obj.getStatus()) {
            case "EACH_LIKED":
                status.setText(getString(R.string.it_is_a_match));
                description.setText(getString(R.string.you_and_liked_each_other, like.getUserLike().getFirstName()));
                if (GlobalData.PROFILE != null) {
                    Glide.with(getActivity()).load(GlobalData.PROFILE.getUser().getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(avatar1);
                }
                Glide.with(getActivity()).load(like.getUserLike().getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(avatar2);

                statusPopupDialog.show();
                break;
            case "SUPERLIKED":
                status.setText(getString(R.string.super_like));
                description.setText(getString(R.string.you_superliked, like.getUserLike().getFirstName()));
                if (GlobalData.PROFILE != null) {
                    Glide.with(getActivity()).load(GlobalData.PROFILE.getUser().getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(avatar1);
                }
                Glide.with(getActivity()).load(like.getUserLike().getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(avatar2);
                statusPopupDialog.show();
                break;
            case "SUPERLIKE_REACHED_LIMIT":
                premiumDialog = new PremiumDialog(getContext());
                premiumDialog.show();
                break;
            default:
                break;
        }
    }
}
