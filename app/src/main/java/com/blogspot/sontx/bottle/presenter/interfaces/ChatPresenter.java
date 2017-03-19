package com.blogspot.sontx.bottle.presenter.interfaces;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.blogspot.sontx.bottle.model.bean.ChatMessage;
import com.blogspot.sontx.bottle.view.activity.ChatActivity;

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

    void setup(Context context);
}
