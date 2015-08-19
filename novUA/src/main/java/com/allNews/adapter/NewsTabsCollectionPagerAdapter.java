package com.allNews.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.allNews.activity.TabFragment;

import java.util.ArrayList;

public class NewsTabsCollectionPagerAdapter extends FragmentStatePagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private ArrayList<String> tabTag;

    public NewsTabsCollectionPagerAdapter(FragmentManager fm,
                                          ArrayList<String> tabTag) {
        super(fm);

        this.tabTag = tabTag;
    }

    @Override
    public Fragment getItem(int i) {
        TabFragment fragment = new TabFragment();
        fragment.setCustomTag(tabTag.get(i));
        Bundle args = new Bundle();
        args.putString(TabFragment.TAB, tabTag.get(i));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return tabTag.size();
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
        try {
            super.destroyItem(container, position, object);
        } catch (Exception e) {
        }
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}