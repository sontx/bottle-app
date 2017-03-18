package com.blogspot.sontx.bottle.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blogspot.sontx.bottle.view.fragment.ChatItemFragment;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
    public HomeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        return ChatItemFragment.newInstance();
//        switch (position) {
//            case 0:
//                return ChatItemFragment.newInstance();
//            case 1:
//                return null;
//            case 2:
//                return null;
//            default:
//                return null;
//        }
    }
}
