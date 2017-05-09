package com.blogspot.sontx.bottle.presenter;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerGeoService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerStompService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatServerLoginService;
import com.blogspot.sontx.bottle.presenter.interfaces.HomePresenter;
import com.blogspot.sontx.bottle.system.provider.Auth2Provider;
import com.blogspot.sontx.bottle.view.interfaces.HomeView;

import java.util.List;

public class HomePresenterImpl extends PresenterBase implements HomePresenter {
    private final HomeView homeView;
    private final BottleServerGeoService bottleServerGeoService;
    private final BottleServerRoomService bottleServerRoomService;

    public HomePresenterImpl(HomeView homeView) {
        this.homeView = homeView;
        bottleServerGeoService = FirebaseServicePool.getInstance().getBottleServerGeoService();
        bottleServerRoomService = FirebaseServicePool.getInstance().getBottleServerRoomService();
    }

    @Override
    public void startChat(Channel channel) {
        if (channel != null && channel.isValid())
            homeView.startChatWithExistingChannel(channel);
    }

    @Override
    public void directMessage(final MessageBase message) {
        final ChannelService channelService = FirebaseServicePool.getInstance().getChannelService();
        channelService.getCurrentChannelsAsync(new Callback<List<Channel>>() {
            @Override
            public void onSuccess(List<Channel> channels) {
                String anotherGuyId = message.getOwner().getId();

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

    @Override
    public void updateMessageAsync(MessageBase messageBase) {
        if (messageBase instanceof RoomMessage) {
            bottleServerRoomService.editRoomMessageAsync((RoomMessage) messageBase, new UpdateMessageHandler<RoomMessage>());
        } else if (messageBase instanceof GeoMessage) {
            bottleServerGeoService.editGeoMessageAsync((GeoMessage) messageBase, new UpdateMessageHandler<GeoMessage>());
        }
    }

    @Override
    public void onClose() {
        FirebaseServicePool.getInstance().getBottleServerStompService().disconnect();
    }

    @Override
    public void logout() {
        BottleServerStompService bottleServerStompService = FirebaseServicePool.getInstance().getBottleServerStompService();
        bottleServerStompService.disconnect();

        FirebaseServicePool.getInstance().clearCached();

        Auth2Provider currentProvider = App.getInstance().getBottleContext().getCurrentAuth2Provider();
        if (currentProvider != null)
            currentProvider.logout();

        ChatServerLoginService chatServerLoginService = FirebaseServicePool.getInstance().getChatServerLoginService();
        chatServerLoginService.signOut(new SimpleCallback<String>() {
            @Override
            public void onCallback(String value) {
                homeView.updateUIAfterLogout();
                Log.d(TAG, "logout");
            }
        });
    }

    private class UpdateMessageHandler<T extends MessageBase> implements Callback<T> {

        @Override
        public void onSuccess(T result) {
            // do nothing
        }

        @Override
        public void onError(Throwable what) {
            homeView.showErrorMessage(what);
        }
    }
}
