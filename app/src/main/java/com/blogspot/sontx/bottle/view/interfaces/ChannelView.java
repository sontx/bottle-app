package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;

public interface ChannelView extends ViewBase {
    void clearChannels();

    void showChannel(Channel channel);
}
