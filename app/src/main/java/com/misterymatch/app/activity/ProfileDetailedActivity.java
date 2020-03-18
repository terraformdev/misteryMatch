package com.misterymatch.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.R;
import com.misterymatch.app.library.Log;
import com.misterymatch.app.model.Likes;
import com.misterymatch.app.model.Report;
import com.misterymatch.app.model.User;
import com.misterymatch.app.model.UserImage;
import com.misterymatch.app.model.UserInterest;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.fragment.CardFragment.swipeView;
import static com.misterymatch.app.utils.GlobalData.api;

public class ProfileDetailedActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.slider_view_pager)
    ViewPager sliderViewPager;
    @BindView(R.id.slider_dots)
    LinearLayout sliderDots;

    SliderAdapter sliderAdapter;
    List<UserImage> userImages;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.age)
    TextView age;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.about)
    TextView about;
    @BindView(R.id.htab_toolbar)
    Toolbar htabToolbar;
    @BindView(R.id.bio_video_layout)
    CardView bioVideoLayout;
    @BindView(R.id.interests_layout)
    FlexboxLayout interestsLayout;
    @BindView(R.id.design_bottom_s)
    LinearLayout designBottomS;
    @BindView(R.id.video_player)
    JZVideoPlayerStandard videoPlayer;
    private int dotsCount;
    private ImageView[] dots;
    private boolean isWhoLikesMeScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detailed);
        ButterKnife.bind(this);
        setSupportActionBar(htabToolbar);
        userImages = new ArrayList<>();
        sliderAdapter = new SliderAdapter(this, userImages);
        sliderViewPager.setAdapter(sliderAdapter);
        sliderViewPager.setCurrentItem(0);
        sliderViewPager.addOnPageChangeListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Boolean isBottomEnabled = extras.getBoolean("isBottomEnabled");
            isWhoLikesMeScreen = extras.getBoolean("whoLikesMe", false);
            if (!isBottomEnabled) {
                designBottomS.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        if (GlobalData.USER != null) {
            User card = GlobalData.USER;

            name.setText(card.getFirstName());
            about.setText(card.getAbout());
            if (card.getAge() != null)
                age.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.age), card.getAge()));
            else
                age.setText(getString(R.string.age) + " " + "N/A");

            if (card.getDistance() != null)
                distance.setText(String.format(Locale.getDefault(), "%.0f Km Away", card.getDistance()));

            if (card.getBioVideo() != null) {
                videoPlayer.setUp(card.getBioVideo(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL);
                bioVideoLayout.setVisibility(View.VISIBLE);
            }else  {
                bioVideoLayout.setVisibility(View.GONE);
            }

            if (card.getUserImages().size() > 0) {
                userImages.clear();
                userImages.addAll(card.getUserImages());
                sliderAdapter.notifyDataSetChanged();
                addBottomDots();
            }

            interestsLayout.removeAllViews();
            for (UserInterest interest : card.getUserInterest()) {
                RelativeLayout tr_head = (RelativeLayout) getLayoutInflater().inflate(R.layout.item_interest, null, false);
                TextView label = (TextView) tr_head.findViewById(R.id.interest_txt);
                label.setText(interest.getInterest().getName());
                interestsLayout.addView(tr_head);
            }

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.rectangle_off));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.rectangle_on));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void addBottomDots() {
        dotsCount = sliderAdapter.getCount();
        dots = new ImageView[dotsCount];
        if (dotsCount == 0)
            return;

        sliderDots.removeAllViews();

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.rectangle_off));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, 0, 0, 0);

            sliderDots.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.rectangle_on));
    }

    @OnClick({R.id.back, R.id.like, R.id.dislike, R.id.super_like})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.like:
                if(isWhoLikesMeScreen){
                    sendStatus("1");
                }else {
                    finish();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(!swipeView.getAdapter().isEmpty()){
                                swipeView.throwRight();
                            }
                        }
                    }, 1000);
                }
                break;
            case R.id.dislike:
                if(isWhoLikesMeScreen){
                    sendStatus("3");
                }else {
                    finish();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(swipeView.getAdapter().isEmpty()) {
                                swipeView.throwLeft();
                            }
                        }
                    }, 1000);
                }
                break;
            case R.id.super_like:

                if(isWhoLikesMeScreen){
                    sendStatus("2");
                }else {
                    finish();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(swipeView.getAdapter().isEmpty()) {
                                swipeView.throwTop();
                            }
                        }
                    }, 1000);
                }
                break;
        }
    }

    private void sendStatus(String status) {

        if (GlobalData.USER == null)
            return;

        User card = GlobalData.USER;
        Log.d(TAG, "Card id:" + card.getId());
        final HashMap<String, String> map = new HashMap<>();
        map.put("like_id", String.valueOf(card.getId()));
        map.put("status", status);
        Log.d(TAG, map.get("like_id") + "" + map.get("status"));
        System.out.println(map);
        String accessToken = SharedHelper.getKey(getApplicationContext(), "access_token");
        Call<Likes> call = api.sendStatus(accessToken, map);
        call.enqueue(new Callback<Likes>() {
            @Override
            public void onResponse(@NonNull Call<Likes> call, @NonNull Response<Likes> response) {
                if (response.isSuccessful()) {
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Likes> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "sendStatus something went wrong" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_detailed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_report) {
            ReportDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ReportDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(getString(R.string.report_this_user).toUpperCase());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item_report);
        arrayAdapter.add(getString(R.string.inappropriate_messages));
        arrayAdapter.add(getString(R.string.inappropriate_photos));
        arrayAdapter.add(getString(R.string.bad_offline_behaviour));
        arrayAdapter.add(getString(R.string.feel_like_spam));

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                switch (which) {
                    case 0:
                        reportUser("bad_message");
                        break;
                    case 1:
                        reportUser("bad_photo");
                        break;
                    case 2:
                        reportUser("bad_behaviour");
                        break;
                    case 3:
                        reportUser("spam");
                        break;
                    default:
                        break;
                }
            }
        });
        builderSingle.show();
    }

    private void reportUser(String reason) {

        if (GlobalData.USER == null) {
            return;
        }
        Integer report_id = GlobalData.USER.getId();

        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Report> call = api.reportUser(accessToken, report_id, reason);
        call.enqueue(new Callback<Report>() {
            @Override
            public void onResponse(@NonNull Call<Report> call, @NonNull Response<Report> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Report> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "updateProfile Something Went Wrong" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    public class SliderAdapter extends PagerAdapter {
        List<UserImage> list;
        Context context;

        public SliderAdapter(Context context, List<UserImage> list) {
            this.list = list;
            this.context = context;

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.slider_item, container, false);
            UserImage obj = list.get(position);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

            Glide.with(getApplicationContext()).load(obj.getImage()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(imageView);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}

