package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.service.Callback;

import java.util.List;

public interface ChannelService extends ServiceBase {
    void getCurrentChannelsAsync(Callback<List<Channel>> callback);

    void getChannelMembersAsync(String channelId, Callback<List<ChannelMember>> callback);

    void getChannelDetailAsync(String channelId, Callback<ChannelDetail> callback);

    Channel createChannel(String anotherMemberId);

    void cacheChannelsAsync(Callback<List<Channel>> callback);

    boolean isCachedChannels();
}
