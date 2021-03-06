package com.blogspot.sontx.bottle.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.presenter.HomePresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.HomePresenter;
import com.blogspot.sontx.bottle.view.adapter.HomeFragmentPagerAdapter;
import com.blogspot.sontx.bottle.view.dialog.GeoMessageDialog;
import com.blogspot.sontx.bottle.view.fragment.ListChannelFragment;
import com.blogspot.sontx.bottle.view.fragment.ListGeoMessageFragment;
import com.blogspot.sontx.bottle.view.fragment.ListRoomMessageFragment;
import com.blogspot.sontx.bottle.view.fragment.OnFragmentVisibleChangedListener;
import com.blogspot.sontx.bottle.view.fragment.SettingFragment;
import com.blogspot.sontx.bottle.view.interfaces.HomeView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends ActivityBase implements
        ListRoomMessageFragment.OnListRoomMessageInteractionListener,
        ListGeoMessageFragment.OnListGeoMessageInteractionListener,
        ListChannelFragment.OnChannelInteractionListener,
        SettingFragment.OnSettingFragmentInteractionListener,
        HomeView,
        OnTabSelectListener,
        ViewPager.OnPageChangeListener {

    @BindView(R.id.vp_horizontal_ntb)
    ViewPager viewPager;
    @BindView(R.id.bottom_bar)
    BottomBar bottomBar;

    private int lastPagePosition = 0;
    private HomePresenter homePresenter;
    private GeoMessageDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        viewPager.setAdapter(new HomeFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(3);

        bottomBar.setOnTabSelectListener(this);

        homePresenter = new HomePresenterImpl(this);
    }

    @Override
    protected void onDestroy() {
        viewPager.removeOnPageChangeListener(this);
        homePresenter.onClose();
        super.onDestroy();
    }

    @Override
    public void onListChannelInteraction(Channel channel) {
        homePresenter.startChat(channel);
    }

    @Override
    public void onListRoomMessageInteraction(RoomMessage item) {

    }

    @Override
    public void onDirectMessageClick(MessageBase item) {
        homePresenter.directMessage(item);
    }

    @Override
    public void onVoteMessageClick(MessageBase item) {

    }

    @Override
    public void onUpdateMessageClick(MessageBase messageBase) {
        homePresenter.updateMessageAsync(messageBase);
    }

    @Override
    public void onDeleteMessageClick(MessageBase messageBase) {
        if (messageBase instanceof RoomMessage) {
            ListRoomMessageFragment fragment = (ListRoomMessageFragment) ((HomeFragmentPagerAdapter) viewPager.getAdapter()).getRegisteredFragment(1);
            fragment.removeMessage(messageBase);
        } else if (messageBase instanceof GeoMessage) {
            if (dialog != null)
                dialog.close();
            ListGeoMessageFragment fragment = (ListGeoMessageFragment) ((HomeFragmentPagerAdapter) viewPager.getAdapter()).getRegisteredFragment(2);
            fragment.removeMessage(messageBase);
        }
    }

    @Override
    public void onGeoMessageClick(GeoMessage item) {
        dialog = new GeoMessageDialog(this, item);
    }

    @Override
    public void startChatWithExistingChannel(Channel channel) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CHANNEL_KEY, channel);
        startActivity(intent);
    }

    @Override
    public void startChatWithExistingChannel(String channelId) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CHANNEL_ID_KEY, channelId);
        startActivity(intent);
    }

    @Override
    public void startChatWithAnotherGuy(String anotherGuyId) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.ANOTHER_GUY_ID_KEY, anotherGuyId);
        startActivity(intent);
    }

    @Override
    public void updateUIAfterLogout() {
        App.getInstance().getBottleContext().clear();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void removeMessageAfterRemoved(final MessageBase messageBase) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message is expired");
        String text = messageBase.getText();
        text = text.length() > 50 ? text.substring(0, 47).concat("...") : text;
        builder.setMessage(String.format("Message \"%s\" is no longer available because it's over 24H.", text));
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (messageBase instanceof RoomMessage) {
                    HomeFragmentPagerAdapter adapter = (HomeFragmentPagerAdapter) viewPager.getAdapter();
                    ListRoomMessageFragment fragment = (ListRoomMessageFragment) adapter.getRegisteredFragment(1);
                    fragment.removeMessage(messageBase);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Keep it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
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
            viewPager.setCurrentItem(3);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        bottomBar.selectTabAtPosition(position, true);
        changeFragmentVisibleStateIfNecessary(position, true);
        if (lastPagePosition > -1)
            changeFragmentVisibleStateIfNecessary(lastPagePosition, false);
        lastPagePosition = position;
    }

    public void changeFragmentVisibleStateIfNecessary(int position, boolean isVisible) {
        HomeFragmentPagerAdapter adapter = (HomeFragmentPagerAdapter) viewPager.getAdapter();
        Fragment fragment = adapter.getRegisteredFragment(position);
        if (fragment instanceof OnFragmentVisibleChangedListener) {
            OnFragmentVisibleChangedListener onFragmentVisibleChangedListener = (OnFragmentVisibleChangedListener) fragment;
            onFragmentVisibleChangedListener.onFragmentVisibleChanged(isVisible);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void logoutClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Logout, are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                homePresenter.logout();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
