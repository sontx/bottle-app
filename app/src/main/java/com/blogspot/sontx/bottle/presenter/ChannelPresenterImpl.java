package com.blogspot.sontx.bottle.presenter;

import android.support.annotation.Nullable;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.service.FirebaseChannelService;
import com.blogspot.sontx.bottle.model.service.FirebasePublicProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.Callback;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.ChannelPresenter;
import com.blogspot.sontx.bottle.view.interfaces.ChannelView;

import java.util.List;

import lombok.NonNull;

public class ChannelPresenterImpl extends PresenterBase implements ChannelPresenter {
    private final ChannelView channelView;
    private final ChannelService channelService;
    private final PublicProfileService publicProfileService;
    private final String currentUserId;
    private List<Channel> channels;

    public ChannelPresenterImpl(ChannelView channelView, @NonNull String currentUserId) {
        this.channelView = channelView;
        this.currentUserId = currentUserId;
        this.channelService = new FirebaseChannelService(channelView.getContext());
        this.publicProfileService = new FirebasePublicProfileService(channelView.getContext());
    }

    @Override
    public void getCurrentChannelsAsync() {
        channelView.clearChannels();

        channelService.getCurrentChannelsAsync(currentUserId, new Callback<List<Channel>>() {
            @Override
            public void onSuccess(List<Channel> result) {
                channels = result;
                if (!channels.isEmpty()) {
                    channelView.addChannels(channels);
                    for (Channel channel : channels) {
                        getChannelDetailAsync(channel);
                        getChannelMembersAsync(channel);
                    }
                }
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
            getPublicProfileAsync(memberList.get(0), channel, false);
            getPublicProfileAsync(memberList.get(1), null, false);
        } else {
            getPublicProfileAsync(memberList.get(0), null, false);
            getPublicProfileAsync(memberList.get(1), channel, false);
        }
    }

    private void getPublicProfileAsync(final ChannelMember channelMember, @Nullable final Channel channel, final boolean isUpdated) {
        publicProfileService.getPublicProfileAsync(channelMember.getId(), new Callback<PublicProfile>() {
            @Override
            public void onSuccess(PublicProfile result) {
                channelMember.setPublicProfile(result);
                if (channel != null) {
                    if (isUpdated)
                        channelView.updateChannel(channel);
                    else
                        channelView.addChannel(channel);
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
                    getPublicProfileAsync(member, channel, true);
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
                channelView.updateChannel(channel);
            }

            @Override
            public void onError(Throwable what) {
                channelView.showErrorMessage(what.getMessage());
            }
        });
    }
}
