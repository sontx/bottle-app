package com.blogspot.sontx.bottle.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.blogspot.sontx.bottle.view.fragment.ListChannelFragment;
import com.blogspot.sontx.bottle.view.fragment.ListGeoMessageFragment;
import com.blogspot.sontx.bottle.view.fragment.ListRoomMessageFragment;
import com.blogspot.sontx.bottle.view.fragment.SettingFragment;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
    private SparseArray<Fragment> fragmentSparseArray;

    public HomeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentSparseArray = new SparseArray<>(getCount());
    }

    public Fragment getRegisteredFragment(int position) {
        return fragmentSparseArray.get(position);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return ListChannelFragment.newInstance();
        if (position == 1)
            return ListRoomMessageFragment.newInstance(1);
        if (position == 2)
            return ListGeoMessageFragment.newInstance();
        if (position == 3)
            return SettingFragment.newInstance();
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragmentSparseArray.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragmentSparseArray.remove(position);
        super.destroyItem(container, position, object);
    }
}
