package com.misterymatch.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.misterymatch.app.fragment.ChatFragment;
import com.misterymatch.app.fragment.HomeFragment;
import com.misterymatch.app.fragment.ProfileFragment;

/**
 * Created by santhosh@appoets.com on 23-11-2017.
 */

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos) {
            case 0:
                return new ProfileFragment();
            case 1:
                return new HomeFragment();
            case 2:
                return new ChatFragment();
            default:
                return new ProfileFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public String makeFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }

}
