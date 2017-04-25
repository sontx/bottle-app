package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.ChannelPresenter;

import java.util.List;

public class ChannelPresenterImpl extends PresenterBase implements ChannelPresenter {

    @Override
    public void resolveChannelAsync(String channelId, final Callback<Channel> callback) {
        ChannelService channelService = FirebaseServicePool.getInstance().getChannelService();
        final Channel channel = new Channel();
        final Object lock = new Object();
        channel.setId(channelId);
        channelService.getChannelDetailAsync(channelId, new Callback<ChannelDetail>() {
            @Override
            public void onSuccess(ChannelDetail result) {
                synchronized (lock) {
                    channel.setDetail(result);
                    if (channel.getMemberList() != null)
                        callback.onSuccess(channel);
                }
            }

            @Override
            public void onError(Throwable what) {
                callback.onError(what);
            }
        });

        channelService.getChannelMembersAsync(channelId, new Callback<List<ChannelMember>>() {
            @Override
            public void onSuccess(List<ChannelMember> result) {
                channel.setMemberList(result);

                PublicProfileService publicProfileService = FirebaseServicePool.getInstance().getPublicProfileService();
                for (final ChannelMember channelMember : result) {
                    publicProfileService.getPublicProfileAsync(channelMember.getId(), new Callback<PublicProfile>() {
                        @Override
                        public void onSuccess(PublicProfile result) {
                            synchronized (lock) {
                                channelMember.setPublicProfile(result);
                                if (channel.getDetail() != null && channel.isFullMembersInfo())
                                    callback.onSuccess(channel);
                            }
                        }

                        @Override
                        public void onError(Throwable what) {
                            callback.onError(what);
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable what) {
                callback.onError(what);
            }
        });
    }
}
