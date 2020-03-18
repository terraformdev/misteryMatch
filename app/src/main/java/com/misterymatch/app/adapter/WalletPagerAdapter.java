package com.misterymatch.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.misterymatch.app.fragment.WalletTransactionAddedFragment;
import com.misterymatch.app.fragment.WalletTransactionPaidFragment;
import com.misterymatch.app.fragment.WalletTransactionAllFragment;
import com.misterymatch.app.fragment.WalletTransactionReceivedFragment;

/**
 * Created by Dell on 18-01-2018.
 */

public class WalletPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public WalletPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new WalletTransactionAllFragment();
            case 1:
                return new WalletTransactionPaidFragment();
            case 2:
                return new WalletTransactionReceivedFragment();
            case 3:
                return new WalletTransactionAddedFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
