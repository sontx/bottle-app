package com.blogspot.sontx.bottle.view.interfaces;

import java.util.List;

import it.slyce.messaging.message.Message;

public interface ChatView extends ViewBase {
    void addNewMessage(Message result);

    void onHasMoreMessages(List<Message> messages);

    void updateChatMessage(Message message, boolean refresh);

    void updateUI();

    void setChatTitle(String displayName);
}
