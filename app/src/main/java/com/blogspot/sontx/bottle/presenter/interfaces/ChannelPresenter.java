package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.service.Callback;

public interface ChannelPresenter {
    void resolveChannelAsync(String channelId, Callback<Channel> callback);
}
