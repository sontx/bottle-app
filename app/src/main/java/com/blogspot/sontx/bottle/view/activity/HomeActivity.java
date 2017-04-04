package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.presenter.ChannelPresenterImpl;
import com.blogspot.sontx.bottle.presenter.HomePresenterImpl;
import com.blogspot.sontx.bottle.presenter.LoginPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.ChannelPresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.HomePresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.view.activity.helper.NavigationTabBarHelper;
import com.blogspot.sontx.bottle.view.fragment.ChannelFragment;
import com.blogspot.sontx.bottle.view.fragment.SettingFragment;
import com.blogspot.sontx.bottle.view.interfaces.ChannelView;
import com.blogspot.sontx.bottle.view.interfaces.HomeView;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends ActivityBase
        implements SettingFragment.OnSettingFragmentInteractionListener, NavigationTabBarHelper.OnViewPagerTabSelectedListener,
        ChannelFragment.OnChannelInteractionListener, LoginView, ChannelView, HomeView {

    private final NavigationTabBarHelper navigationTabBarHelper = new NavigationTabBarHelper(this);
    @BindView(R.id.vp_horizontal_ntb)
    ViewPager viewPager;
    private LoginPresenter loginPresenter;
    private ChannelPresenter channelPresenter;
    private HomePresenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        homePresenter = new HomePresenterImpl(this);
        loginPresenter = new LoginPresenterImpl(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        homePresenter.onStart();
        loginPresenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        homePresenter.onResume();
        loginPresenter.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        homePresenter.onStop();
        loginPresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        homePresenter.onDestroy();
    }

    @Override
    public void updateUI(String userId) {
        if (userId == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (channelPresenter == null || !channelPresenter.getCurrentUserId().equalsIgnoreCase(userId)) {
            navigationTabBarHelper.initializeViewPager(userId, viewPager);
            navigationTabBarHelper.setOnViewPagerTabSelectedListener(this);

            homePresenter.switchCurrentUserId(userId);
            channelPresenter = new ChannelPresenterImpl(this, userId);

            channelPresenter.updateChannelsIfNecessary();
        }
    }

    @Override
    public void clearChannels() {
        final Fragment fragment = navigationTabBarHelper.getFragment(0);
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
    public void showChannel(final Channel channel) {
        final Fragment fragment = navigationTabBarHelper.getFragment(0);
        if (fragment instanceof ChannelFragment) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ChannelFragment) fragment).showChannel(channel);
                }
            });
        }
    }

    @Override
    public void showChannels(final List<Channel> channels) {
        final Fragment fragment = navigationTabBarHelper.getFragment(0);
        if (fragment instanceof ChannelFragment) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ChannelFragment) fragment).showChannels(channels);
                }
            });
        } else {
            ChannelFragment.setTempList(channels);
        }
    }

    @Override
    public void logoutClick() {
        loginPresenter.logout();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void chatClick(String anotherGuyId) {
        if (channelPresenter != null)
            channelPresenter.createChannelAsync(anotherGuyId);
    }

    @Override
    public void onChannelInteraction(Channel channel) {
        homePresenter.startChat(channel);
    }

    @Override
    public void startChat(Channel channel, String currentUserId) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CHANNEL_KEY, channel);
        intent.putExtra(ChatActivity.CURRENT_USER_ID_KEY, currentUserId);
        startActivity(intent);
    }

    @Override
    public void onViewPagerTabSelected(Fragment fragment) {
        //if (fragment instanceof ChannelFragment && channelPresenter != null)
        //channelPresenter.updateChannelsIfNecessary();
    }
}
