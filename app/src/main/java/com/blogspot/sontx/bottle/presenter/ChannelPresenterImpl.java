package com.blogspot.sontx.bottle.presenter;

import android.support.annotation.Nullable;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.ChannelPresenter;
import com.blogspot.sontx.bottle.view.interfaces.ChannelView;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;

public class ChannelPresenterImpl extends PresenterBase implements ChannelPresenter {
    private final ChannelView channelView;
    private final ChannelService channelService;
    private final PublicProfileService publicProfileService;
    @Getter
    private final String currentUserId;
    private List<Channel> channels;
    private boolean isUpdatedChannels = false;

    public ChannelPresenterImpl(ChannelView channelView, @NonNull String currentUserId) {
        this.channelView = channelView;
        this.currentUserId = currentUserId;
        this.channelService = FirebaseServicePool.getInstance().getChannelService();
        this.publicProfileService = FirebaseServicePool.getInstance().getPublicProfileService();
    }

    @Override
    public void updateChannelsIfNecessary() {
        if (isUpdatedChannels && channels != null)
            return;

        channelView.clearChannels();

        channelService.getCurrentChannelsAsync(currentUserId, new Callback<List<Channel>>() {
            @Override
            public void onSuccess(List<Channel> result) {
                channels = result;
                if (channelService.isCachedChannels()) {
                    channelView.showChannels(channels);
                } else if (!channels.isEmpty()) {
                    ChatService chatService = FirebaseServicePool.getInstance().getChatService();
                    for (Channel channel : channels) {
                        chatService.registerChannel(channel.getId());
                        getChannelDetailAsync(channel);
                        getChannelMembersAsync(channel);
                    }
                }
                isUpdatedChannels = true;
            }

            @Override
            public void onError(Throwable what) {
                channelView.showErrorMessage(what.getMessage());
            }
        });
    }

    @Override
    public void createChannelAsync(final String anotherMemberId) {
        final Channel channel = channelService.createChannel(currentUserId, anotherMemberId);

        List<ChannelMember> memberList = channel.getMemberList();
        if (memberList.get(0).getId().equals(anotherMemberId)) {
            getPublicProfileAsync(memberList.get(0), channel);
            getPublicProfileAsync(memberList.get(1), null);
        } else {
            getPublicProfileAsync(memberList.get(0), null);
            getPublicProfileAsync(memberList.get(1), channel);
        }
    }

    private void getPublicProfileAsync(final ChannelMember channelMember, @Nullable final Channel channel) {
        publicProfileService.getPublicProfileAsync(channelMember.getId(), new Callback<PublicProfile>() {
            @Override
            public void onSuccess(PublicProfile result) {
                channelMember.setPublicProfile(result);
                if (channel != null) {
                    channelView.showChannel(channel);
                }
            }

            @Override
            public void onError(Throwable what) {
                channelView.showErrorMessage(what.getMessage());
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
                channelView.showErrorMessage(what.getMessage());
            }
        });
    }

    private void getChannelDetailAsync(final Channel channel) {
        channelService.getChannelDetailAsync(channel.getId(), new Callback<ChannelDetail>() {
            @Override
            public void onSuccess(ChannelDetail result) {
                channel.setDetail(result);
                channelView.showChannel(channel);
            }

            @Override
            public void onError(Throwable what) {
                channelView.showErrorMessage(what.getMessage());
            }
        });
    }
}
