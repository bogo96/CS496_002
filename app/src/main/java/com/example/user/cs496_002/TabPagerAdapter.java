package com.example.user.cs496_002;

/**
 * Created by user on 2017-12-30.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        // Returning the current tabs
        switch (position) {
            case 0:
                Fragment1 tabFragment1 = new Fragment1();
                return tabFragment1;
            case 1:
                Fragment2 tabFragment2 = new Fragment2();
                return tabFragment2;
            case 2:
                Fragment3 tabFragment3 = new Fragment3();
                return tabFragment3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}