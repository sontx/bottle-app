package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.presenter.ChannelPresenterImpl;
import com.blogspot.sontx.bottle.presenter.LoginPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.ChannelPresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.view.activity.helper.NavigationTabBarHelper;
import com.blogspot.sontx.bottle.view.fragment.ChannelFragment;
import com.blogspot.sontx.bottle.view.fragment.SettingFragment;
import com.blogspot.sontx.bottle.view.interfaces.ChannelView;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends ActivityBase
        implements SettingFragment.OnSettingFragmentInteractionListener, ChannelFragment.OnChannelInteractionListener, NavigationTabBarHelper.OnViewPagerTabSelectedListener, LoginView, ChannelView {

    @BindView(R.id.vp_horizontal_ntb)
    ViewPager viewPager;
    private LoginPresenter loginPresenter;
    private ChannelPresenter channelPresenter;
    private final NavigationTabBarHelper navigationTabBarHelper = new NavigationTabBarHelper(this);
    private boolean requestedChannels = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        loginPresenter = new LoginPresenterImpl(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginPresenter.checkLoginState();
    }

    @Override
    public void updateUI(FirebaseUser user) {
        if (user == null) {
            startActivity(new Intent(this, FbLoginActivity.class));
            finish();
        } else {
            String currentUserId = user.getUid();
            channelPresenter = new ChannelPresenterImpl(this, currentUserId);
            navigationTabBarHelper.initializeViewPager(currentUserId, viewPager);
            navigationTabBarHelper.setOnViewPagerTabSelectedListener(this);
        }
    }

    @Override
    public void clearChannels() {
        final Fragment fragment = navigationTabBarHelper.getCurrentFragment();
        if (fragment instanceof ChannelFragment) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ChannelFragment) fragment).clearChannels();
                }
            });
        }
    }

    @Override
    public void addChannels(final List<Channel> channels) {
        final Fragment fragment = navigationTabBarHelper.getCurrentFragment();
        if (fragment instanceof ChannelFragment) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ChannelFragment) fragment).addChannels(channels);
                }
            });
        }
    }

    @Override
    public void updateChannel(final Channel channel) {
        final Fragment fragment = navigationTabBarHelper.getCurrentFragment();
        if (fragment instanceof ChannelFragment) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ChannelFragment) fragment).updateChannel(channel);
                }
            });
        }
    }

    @Override
    public void addChannel(final Channel channel) {
        final Fragment fragment = navigationTabBarHelper.getCurrentFragment();
        if (fragment instanceof ChannelFragment) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ChannelFragment) fragment).addChannel(channel);
                }
            });
        }
    }

    @Override
    public void logoutClick() {
        loginPresenter.logout();
        startActivity(new Intent(this, FbLoginActivity.class));
    }

    @Override
    public void chatClick(String anotherGuyId) {
        if (channelPresenter != null)
            channelPresenter.createChannelAsync(anotherGuyId);
    }

    @Override
    public void onChannelInteraction(Channel channel) {

    }

    @Override
    public void onViewPagerTabSelected(Fragment fragment) {
        if (channelPresenter != null && !requestedChannels) {
            requestedChannels = true;
            channelPresenter.getCurrentChannelsAsync();
        }
    }
}
