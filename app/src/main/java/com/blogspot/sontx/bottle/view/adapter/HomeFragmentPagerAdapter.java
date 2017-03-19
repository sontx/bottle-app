package com.blogspot.sontx.bottle.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blogspot.sontx.bottle.view.fragment.ListChatChannelFragment;
import com.google.firebase.auth.FirebaseAuth;

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
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return ListChatChannelFragment.newInstance(uid);
//        switch (position) {
//            case 0:
//                return ListChatChannelFragment.newInstance();
//            case 1:
//                return null;
//            case 2:
//                return null;
//            default:
//                return null;
//        }
    }
}
