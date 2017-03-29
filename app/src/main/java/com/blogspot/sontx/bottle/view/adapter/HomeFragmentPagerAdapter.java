package com.blogspot.sontx.bottle.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blogspot.sontx.bottle.view.fragment.ChannelFragment;
import com.blogspot.sontx.bottle.view.fragment.SettingFragment;
import com.squareup.picasso.Picasso;

import lombok.Setter;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {

    @Setter
    private String currentUserId;
    private Fragment[] fragments;

    public HomeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void initialize() {
        fragments = new Fragment[]{ChannelFragment.newInstance(currentUserId), SettingFragment.newInstance()};
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }
}
