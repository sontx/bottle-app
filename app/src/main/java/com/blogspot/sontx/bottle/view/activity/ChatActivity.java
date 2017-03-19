package com.blogspot.sontx.bottle.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.presenter.ChatPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.ChatPresenter;
import com.blogspot.sontx.bottle.view.interfaces.ChatView;

import java.util.ArrayList;
import java.util.List;

import it.slyce.messaging.SlyceMessagingFragment;
import it.slyce.messaging.listeners.LoadMoreMessagesListener;
import it.slyce.messaging.listeners.UserSendsMessageListener;
import it.slyce.messaging.message.Message;

public class ChatActivity extends ActivityBase implements ChatView, UserSendsMessageListener, LoadMoreMessagesListener {
    static final String RECIPIENT_USER_ID_KEY = "recipient_user_id";
    static final String CURRENT_USER_ID_KEY = "current_user_id";

    private ChatPresenter chatPresenter;
    private SlyceMessagingFragment slyceMessagingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String recipientUserId = extras.getString(RECIPIENT_USER_ID_KEY);
            String currentUserId = extras.getString(CURRENT_USER_ID_KEY);

            chatPresenter = new ChatPresenterImpl(this);
            chatPresenter.setRecipientUserId(recipientUserId);
            chatPresenter.setCurrentUserId(currentUserId);
            chatPresenter.setup(this);

            initUI();
        } else {
            finish();
        }
    }

    private void initUI() {
        slyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.fragment_for_slyce_messaging);
        slyceMessagingFragment.setDefaultAvatarUrl(chatPresenter.getDefaultAvatarUrl());
        slyceMessagingFragment.setDefaultDisplayName(chatPresenter.getDefaultDisplayName());

        slyceMessagingFragment.setOnSendMessageListener(this);
        slyceMessagingFragment.setLoadMoreMessagesListener(this);
    }

    @Override
    public void onUserSendsTextMessage(String text) {
        Log.d(TAG, "send-text: " + text);
        chatPresenter.sendAsync(text);
    }

    @Override
    public void onUserSendsMediaMessage(Uri imageUri) {
        Log.d(TAG, "send-media: " + imageUri);
        chatPresenter.sendAsync(imageUri);
    }

    @Override
    public List<Message> loadMoreMessages() {
        Log.d(TAG, "load more...");
        List<Message> messages = chatPresenter.getMoreMessages();
        slyceMessagingFragment.setMoreMessagesExist(chatPresenter.isMoreMessagesExist());
        return messages;
    }

    @Override
    public void displayMessage(final Message result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                slyceMessagingFragment.addNewMessage(result);
            }
        });
    }
}
