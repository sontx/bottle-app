package com.blogspot.sontx.bottle.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.presenter.ChatPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.ChatPresenter;
import com.blogspot.sontx.bottle.view.interfaces.ChatView;

import java.util.List;

import it.slyce.messaging.SlyceMessagingFragment;
import it.slyce.messaging.listeners.LoadMoreMessagesListener;
import it.slyce.messaging.listeners.UserSendsMessageListener;
import it.slyce.messaging.message.Message;

public class ChatActivity extends ActivityBase implements ChatView, UserSendsMessageListener, LoadMoreMessagesListener {
    static final String CHANNEL_KEY = "channel";
    static final String CURRENT_USER_ID_KEY = "current_user_id";

    private ChatPresenter chatPresenter;
    private SlyceMessagingFragment slyceMessagingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        if (getIntent() != null) {
            Channel channel = (Channel) getIntent().getSerializableExtra(CHANNEL_KEY);
            String currentUserId = getIntent().getStringExtra(CURRENT_USER_ID_KEY);

            chatPresenter = new ChatPresenterImpl(this, currentUserId);
            chatPresenter.setChannel(channel);

            initializeChatFragment();
        } else {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatPresenter.onStop();
    }

    @Override
    public void onUserSendsTextMessage(String text) {
        Log.d(TAG, "send-text: " + text);
        chatPresenter.sendAsync(text);
    }

    @Override
    public void onUserSendsMediaMessage(Uri imageUri) {
        Log.d(TAG, "send-media: " + imageUri);
    }

    @Override
    public void addNewMessage(final Message result) {
        slyceMessagingFragment.addNewMessage(result);
    }

    @Override
    public void onHasMoreMessages(List<Message> messages) {
        slyceMessagingFragment.loadMoreMessages(messages);
    }

    @Override
    public void requestLoadMoreMessages() {
        chatPresenter.requestLoadMoreMessages();
    }

    private void initializeChatFragment() {
        PublicProfile currentUserProfile = chatPresenter.getCurrentPublicProfile();

        slyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.fragment_for_slyce_messaging);
        slyceMessagingFragment.setDefaultAvatarUrl(currentUserProfile.getAvatarUrl());
        slyceMessagingFragment.setDefaultDisplayName(currentUserProfile.getDisplayName());

        slyceMessagingFragment.setOnSendMessageListener(this);
        slyceMessagingFragment.setLoadMoreMessagesListener(this);
    }
}
