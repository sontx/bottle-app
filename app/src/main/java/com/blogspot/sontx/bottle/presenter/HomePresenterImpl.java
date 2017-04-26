package com.blogspot.sontx.bottle.presenter;

import android.util.Log;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.presenter.interfaces.HomePresenter;
import com.blogspot.sontx.bottle.view.interfaces.HomeView;

import java.util.List;

public class HomePresenterImpl extends PresenterBase implements HomePresenter {
    private final HomeView homeView;

    public HomePresenterImpl(HomeView homeView) {
        this.homeView = homeView;
    }

    @Override
    public void startChat(Channel channel) {
        if (channel != null && channel.isValid())
            homeView.startChatWithExistingChannel(channel);
    }

    @Override
    public void directMessage(final RoomMessage roomMessage) {
        final ChannelService channelService = FirebaseServicePool.getInstance().getChannelService();
        channelService.getCurrentChannelsAsync(new Callback<List<Channel>>() {
            @Override
            public void onSuccess(List<Channel> channels) {
                String anotherGuyId = roomMessage.getOwner().getId();

                for (Channel channel : channels) {
                    PublicProfile anotherGuyPublicProfile = channel.getAnotherGuy().getPublicProfile();

                    if (anotherGuyPublicProfile != null && anotherGuyPublicProfile.getId().equals(anotherGuyId)) {
                        startChat(channel);
                        return;
                    }
                }

                homeView.startChatWithAnotherGuy(anotherGuyId);
                Log.d(TAG, "create chat channel with " + anotherGuyId);
            }

            @Override
            public void onError(Throwable what) {
            }
        });
    }
}
