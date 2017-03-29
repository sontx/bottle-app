package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;

import java.util.List;

public interface ChannelView extends ViewBase {
    void clearChannels();

    void addChannels(List<Channel> channels);

    void updateChannel(Channel channel);

    void addChannel(Channel channel);
}
