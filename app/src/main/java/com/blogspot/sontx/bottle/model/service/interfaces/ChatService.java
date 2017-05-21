package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;

import java.util.List;

public interface ChatService extends ServiceBase {
    boolean isRegisteredChatMessageListeners();

    void getMoreMessages(String channelId, long startAt, int limit, Callback<List<ChatMessage>> callback);

    void registerChannel(Channel channel);

    void unregisterChannel(String channelId);

    void unregisterAllChannels();

    void sendAsync(String channelId, String text, Callback<ChatMessage> callback);

    void setOnNewChatMessage(SimpleCallback<ChatMessage> callback);

    void setOnChatMessageChanged(SimpleCallback<ChatMessage> callback);

    void updateChatMessageStateAsync(String channelId, String id, String newState);
}
