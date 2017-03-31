package com.blogspot.sontx.bottle.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blogspot.sontx.bottle.view.fragment.ChannelFragment;
import com.blogspot.sontx.bottle.view.fragment.SettingFragment;

import lombok.Getter;
import lombok.Setter;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {

    @Setter
    private String currentUserId;
    @Getter
    private Fragment currentFragment = null;

    public HomeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return currentFragment = ChannelFragment.newInstance(currentUserId);

        return currentFragment = SettingFragment.newInstance();
    }
}
