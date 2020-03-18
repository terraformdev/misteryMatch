package com.misterymatch.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.misterymatch.app.MyApplication;
import com.misterymatch.app.R;
import com.misterymatch.app.adapter.WalletPagerAdapter;
import com.misterymatch.app.model.User;
import com.misterymatch.app.utils.GlobalData;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by santhosh@appoets.com on 19-01-2018.
 */

public class WalletActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.share_money)
    TextView shareMoney;
    @BindView(R.id.add_money)
    TextView addMoney;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.history_view_pager)
    ViewPager historyViewPager;

    WalletPagerAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title.setText(getString(R.string.wallet));

        tabs.addTab(tabs.newTab().setText(getString(R.string.wallet_all_history)));
        tabs.addTab(tabs.newTab().setText(getString(R.string.wallet_paid_history)));
        tabs.addTab(tabs.newTab().setText(getString(R.string.wallet_received_history)));
        tabs.addTab(tabs.newTab().setText(getString(R.string.wallet_added_history)));


        adapter = new WalletPagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
        historyViewPager.setAdapter(adapter);
        historyViewPager.canScrollHorizontally(0);
        historyViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                historyViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        if(GlobalData.PROFILE != null){
            User user = GlobalData.PROFILE.getUser();
            walletBalance.setText(MyApplication.numberFormat.format(user.getWalletBalance()));
            adapter.notifyDataSetChanged();
        }
    }


    @OnClick({R.id.share_money, R.id.add_money,R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.share_money:
                startActivity(new Intent(this, ShareMoneyActivity.class));
                break;
            case R.id.add_money:
                startActivity(new Intent(this, AddMoneyActivity.class));
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet;
    }
}
