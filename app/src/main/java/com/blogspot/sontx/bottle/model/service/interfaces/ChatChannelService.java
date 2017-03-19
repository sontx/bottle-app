package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.ChatChannelInfo;

public interface ChatChannelService {
    void setup(String userId);

    void setOnNewChatChannel(Callback<ChatChannelInfo> simpleCallback);

    void fetchAvailableChatChannelsAsync();
}
