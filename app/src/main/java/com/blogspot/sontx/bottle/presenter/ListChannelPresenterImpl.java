package com.blogspot.sontx.bottle.presenter;

import android.support.annotation.Nullable;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.ListChannelPresenter;
import com.blogspot.sontx.bottle.system.event.ChatChannelAddedEvent;
import com.blogspot.sontx.bottle.system.event.ChatChannelChangedEvent;
import com.blogspot.sontx.bottle.view.interfaces.ListChannelView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ListChannelPresenterImpl extends PresenterBase implements ListChannelPresenter {
    private final ListChannelView listChannelView;
    private final ChannelService channelService;
    private final PublicProfileService publicProfileService;
    private List<Channel> channels;
    private boolean isUpdatedChannels = false;

    public ListChannelPresenterImpl(ListChannelView ListChannelView) {
        this.listChannelView = ListChannelView;
        this.channelService = FirebaseServicePool.getInstance().getChannelService();
        this.publicProfileService = FirebaseServicePool.getInstance().getPublicProfileService();
    }

    @Override
    public void updateChannelsIfNecessary() {
        if (isUpdatedChannels && channels != null)
            return;

        if (!App.getInstance().getBottleContext().isLogged())
            return;

        listChannelView.clearChannels();

        channelService.getCurrentChannelsAsync(new Callback<List<Channel>>() {
            @Override
            public void onSuccess(List<Channel> result) {
                channels = result;
                if (channelService.isCachedChannels()) {
                    listChannelView.showChannels(channels);
                } else if (!channels.isEmpty()) {
                    for (Channel channel : channels) {
                        getChannelDetailAsync(channel);
                        getChannelMembersAsync(channel);
                    }
                }

                ChatService chatService = FirebaseServicePool.getInstance().getChatService();
                for (Channel channel : channels) {
                    chatService.registerChannel(channel);
                }
                isUpdatedChannels = true;
            }

            @Override
            public void onError(Throwable what) {
                listChannelView.showErrorMessage(what);
            }
        });
    }

    @Override
    public void registerEvents() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void unregisterEvents() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void createChannelAsync(final String anotherMemberId) {
        final Channel channel = channelService.createChannel(anotherMemberId);

        List<ChannelMember> memberList = channel.getMemberList();
        if (memberList.get(0).getId().equals(anotherMemberId)) {
            getPublicProfileAsync(memberList.get(0), channel);
            getPublicProfileAsync(memberList.get(1), null);
        } else {
            getPublicProfileAsync(memberList.get(0), null);
            getPublicProfileAsync(memberList.get(1), channel);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatChannelAddedEvent(ChatChannelAddedEvent chatChannelAddedEvent) {
        Channel channel = chatChannelAddedEvent.getChannel();
        ChatService chatService = FirebaseServicePool.getInstance().getChatService();
        chatService.registerChannel(channel);
        listChannelView.showChannel(channel);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatChannelChangedEvent(ChatChannelChangedEvent chatChannelChangedEvent) {
        listChannelView.showChannel(chatChannelChangedEvent.getChannel());
    }

    private void getPublicProfileAsync(final ChannelMember channelMember, @Nullable final Channel channel) {
        publicProfileService.getPublicProfileAsync(channelMember.getId(), new Callback<PublicProfile>() {
            @Override
            public void onSuccess(PublicProfile result) {
                channelMember.setPublicProfile(result);
                if (channel != null) {
                    listChannelView.showChannel(channel);
                }
            }

            @Override
            public void onError(Throwable what) {
                listChannelView.showErrorMessage(what);
            }
        });
    }

    private void getChannelMembersAsync(final Channel channel) {
        channelService.getChannelMembersAsync(channel.getId(), new Callback<List<ChannelMember>>() {

            @Override
            public void onSuccess(List<ChannelMember> result) {
                channel.setMemberList(result);
                for (ChannelMember member : result) {
                    getPublicProfileAsync(member, channel);
                }
            }

            @Override
            public void onError(Throwable what) {
                listChannelView.showErrorMessage(what);
            }
        });
    }

    private void getChannelDetailAsync(final Channel channel) {
        channelService.getChannelDetailAsync(channel.getId(), new Callback<ChannelDetail>() {
            @Override
            public void onSuccess(ChannelDetail result) {
                channel.setDetail(result);
                listChannelView.showChannel(channel);
            }

            @Override
            public void onError(Throwable what) {
                listChannelView.showErrorMessage(what);
            }
        });
    }
}
