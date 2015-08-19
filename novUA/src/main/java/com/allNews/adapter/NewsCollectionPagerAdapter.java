package com.allNews.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.allNews.activity.NewsFragment;

import java.util.ArrayList;

public class NewsCollectionPagerAdapter extends FragmentStatePagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private ArrayList<Integer> newsIDs;

    public NewsCollectionPagerAdapter(FragmentManager fm,
                                      ArrayList<Integer> newsIDs) {
        super(fm);

        this.newsIDs = newsIDs;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putLong(NewsFragment.ARG_OBJECT, newsIDs.get(i));
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public int getCount() {

        return newsIDs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}