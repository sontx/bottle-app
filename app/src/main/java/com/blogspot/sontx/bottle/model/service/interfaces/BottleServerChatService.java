package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.CreateChannelResult;
import com.blogspot.sontx.bottle.model.service.Callback;

public interface BottleServerChatService extends ServiceBase {

    void createChannelAsync(String anotherGuyId, Callback<CreateChannelResult> callback);

    void deleteChannelAsync(String channelId, Callback<Void> callback);
}
