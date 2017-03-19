package com.blogspot.sontx.bottle.model.service.interfaces;

import android.net.Uri;

import com.blogspot.sontx.bottle.model.bean.ChatMessage;

import java.util.List;

import it.slyce.messaging.message.Message;

public interface ChatService {
    void sendAsync(String text, Callback<ChatMessage> callback);

    void sendAsync(Uri imageUri, Callback<ChatMessage> callback);

    List<Message> getMoreMessages();
    
    void setup(String channelKey, String user1, String user2);

    void setOnNewChatMessage(SimpleCallback<ChatMessage> callback);
}
