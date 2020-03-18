package com.misterymatch.app.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.misterymatch.app.R;
import com.misterymatch.app.model.Walkthrough;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.misterymatch.app.fragment.MapFragment.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class WalkthroughActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.layoutDots)
    LinearLayout layoutDots;
    @BindView(R.id.skip)
    TextView skip;
    @BindView(R.id.sign_in)
    Button signIn;
    @BindView(R.id.sign_up)
    Button signUp;
    MyViewPagerAdapter adapter;
    private int dotsCount;
    private int[] drawables;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        ButterKnife.bind(this);

        drawables = new int[]{
                R.drawable.walk_1,
                R.drawable.walk_2,
                R.drawable.walk_3,
        };

        List<Walkthrough> list = new ArrayList<>();
        list.add(new Walkthrough(R.drawable.walk_1, getString(R.string.walk_1), getString(R.string.walk_1_description)));
        list.add(new Walkthrough(R.drawable.walk_2, getString(R.string.walk_2), getString(R.string.walk_2_description)));
        list.add(new Walkthrough(R.drawable.walk_3, getString(R.string.walk_3), getString(R.string.walk_3_description)));

        adapter = new MyViewPagerAdapter(this, list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
        addBottomDots();

        getPermision();

    }

    @OnClick({R.id.skip, R.id.sign_in, R.id.sign_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.skip:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.sign_in:
                startActivity(new Intent(this, SignInActivity.class));
                break;
            case R.id.sign_up:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void addBottomDots() {
        dotsCount = adapter.getCount();
        dots = new ImageView[dotsCount];
        if (dotsCount == 0)
            return;

        layoutDots.removeAllViews();

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 4, 4, 4);

            layoutDots.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }

    @OnClick(R.id.skip)
    void skip() {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        List<Walkthrough> list;
        Context context;

        public MyViewPagerAdapter(Context context, List<Walkthrough> list) {
            this.list = list;
            this.context = context;

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pager_item, container, false);
            Walkthrough walk = list.get(position);

            TextView title = (TextView) itemView.findViewById(R.id.title);
            TextView description = (TextView) itemView.findViewById(R.id.description);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);

            title.setText(walk.title);
            description.setText(walk.description);
            Glide.with(getApplicationContext()).load(walk.drawable).into(imageView);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public int getCount() {
            return drawables.length;
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

    public void getPermision() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}
