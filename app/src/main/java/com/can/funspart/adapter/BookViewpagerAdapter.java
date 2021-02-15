package com.can.funspart.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.can.funspart.viewimpl.book.BookContentFragment;

public class BookViewpagerAdapter extends FragmentStatePagerAdapter {

    private String[] mTitles;
    //private List<Fragment> mFragments;

    public BookViewpagerAdapter(FragmentManager fm, String[] mTitles) {
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

        return BookContentFragment.newInstance(position, mTitles[position]);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }
}
