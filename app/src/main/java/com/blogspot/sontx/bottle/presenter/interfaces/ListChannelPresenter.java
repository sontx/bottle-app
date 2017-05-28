package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;

public interface ListChannelPresenter {
    void updateChannelsIfNecessary();

    void registerEvents();

    void unregisterEvents();

    void deleteChannelAsync(Channel channel);
}
