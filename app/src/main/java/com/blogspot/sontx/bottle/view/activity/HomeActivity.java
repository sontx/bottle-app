package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.presenter.HomePresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.HomePresenter;
import com.blogspot.sontx.bottle.view.adapter.HomeFragmentPagerAdapter;
import com.blogspot.sontx.bottle.view.fragment.ListChannelFragment;
import com.blogspot.sontx.bottle.view.fragment.ListRoomMessageFragment;
import com.blogspot.sontx.bottle.view.interfaces.HomeView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends ActivityBase
        implements ListRoomMessageFragment.OnListRoomMessageInteractionListener,
        ListChannelFragment.OnChannelInteractionListener, HomeView, OnTabSelectListener {

    @BindView(R.id.vp_horizontal_ntb)
    ViewPager viewPager;
    @BindView(R.id.bottom_bar)
    BottomBar bottomBar;

    private HomePresenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        viewPager.setAdapter(new HomeFragmentPagerAdapter(getSupportFragmentManager()));

        bottomBar.setOnTabSelectListener(this);

        homePresenter = new HomePresenterImpl(this);
    }

    @Override
    public void onListChannelInteraction(Channel channel) {
        homePresenter.startChat(channel);
    }

    @Override
    public void onListRoomMessageInteraction(RoomMessage item) {

    }

    @Override
    public void startChat(Channel channel) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CHANNEL_KEY, channel);
        startActivity(intent);
    }

    @Override
    public void onTabSelected(@IdRes int tabId) {

        if (tabId == R.id.tab_chat) {
            changeStatusBarColor(R.color.tab_chat_color);
            viewPager.setCurrentItem(0);
        } else if (tabId == R.id.tab_room) {
            changeStatusBarColor(R.color.tab_room_color);
            viewPager.setCurrentItem(1);
        } else if (tabId == R.id.tab_map) {
            changeStatusBarColor(R.color.tab_map_color);
            viewPager.setCurrentItem(2);
        } else if (tabId == R.id.tab_setting) {
            changeStatusBarColor(R.color.tab_setting_color);
            viewPager.setCurrentItem(2);
        }
    }

    private void changeStatusBarColor(int colorId) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(colorId));
        }
    }
}
