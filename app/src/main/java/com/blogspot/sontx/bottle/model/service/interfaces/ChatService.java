package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;

import java.util.List;

public interface ChatService extends ServiceBase {
    void getMoreMessages(String channelId, long startAt, int limit, Callback<List<ChatMessage>> callback);

    void registerChannel(String channelId);

    void unregisterChannel(String channelId);

    void unregisterAllChannels();

    void sendAsync(String channelId, String text, Callback<ChatMessage> callback);

    void setOnNewChatMessage(SimpleCallback<ChatMessage> callback);

    void setOnChatMessageChanged(SimpleCallback<ChatMessage> callback);

    void setCurrentUserId(String currentUserId);
}
