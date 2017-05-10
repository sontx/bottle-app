package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;

import java.util.List;

import it.slyce.messaging.message.Message;

public interface ChatView extends ViewBase {
    void addNewMessage(Message result);

    void onHasMoreMessages(List<Message> messages);

    void updateChatMessage(Message message, boolean refresh);

    void setChatTitle(String displayName);

    void closeAfterDeletedChannel(Channel channel);
}
