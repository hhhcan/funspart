package com.can.funspart.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.can.funspart.viewimpl.news.NewsContentFragment;


public class NewsViewpagerAdapter extends FragmentStatePagerAdapter {

    private String[] mTitles;
    //private List<Fragment> mFragments;

    public NewsViewpagerAdapter(FragmentManager fm, String[] mTitles) {
        super(fm);
        this.mTitles = mTitles;
        // this.mFragments = mFragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return NewsContentFragment.newInstance(position, mTitles[position]);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }
}
