package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;

import java.util.List;

public interface ChannelView extends ViewBase {
    void clearChannels();

    void showChannel(Channel channel);

    void showChannels(List<Channel> channels);
}
