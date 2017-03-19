package com.blogspot.sontx.bottle.presenter.interfaces;

import android.net.Uri;

import java.util.List;

import it.slyce.messaging.message.Message;

public interface ChatPresenter {
    String getDefaultAvatarUrl();

    String getDefaultDisplayName();

    void sendAsync(String text);

    void sendAsync(Uri imageUri);

    void setRecipientUserId(String id);

    void setCurrentUserId(String id);

    List<Message> getMoreMessages();

    boolean isMoreMessagesExist();

    void setup();
}
