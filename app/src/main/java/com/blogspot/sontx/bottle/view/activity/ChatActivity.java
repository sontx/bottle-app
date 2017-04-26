package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.blogspot.sontx.bottle.App;
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
    static final String CHANNEL_ID_KEY = "channel-id";

    private ChatPresenter chatPresenter;
    private SlyceMessagingFragment slyceMessagingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupToolbar();

        slyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.fragment_for_slyce_messaging);

        Intent intent = getIntent();
        if (intent != null) {
            String currentUserId = App.getInstance().getBottleContext().getCurrentBottleUser().getUid();
            chatPresenter = new ChatPresenterImpl(this, currentUserId);

            if (intent.hasExtra(CHANNEL_KEY)) {
                Channel channel = (Channel) intent.getSerializableExtra(CHANNEL_KEY);
                chatPresenter.setChannel(channel);
            } else if (intent.hasExtra(CHANNEL_ID_KEY)) {
                String channelId = intent.getStringExtra(CHANNEL_ID_KEY);
                chatPresenter.setChannelId(channelId);
            } else {
                finish();
            }

        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatPresenter.registerListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatPresenter.unregisterListeners();
    }

    @Override
    public void onUserSendsTextMessage(String text, int internalId) {
        Log.d(TAG, "send-text: " + text);
        chatPresenter.sendAsync(text, internalId);
    }

    @Override
    public void onUserSendsMediaMessage(Uri imageUri, int internalId) {
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
    public void updateChatMessage(Message message, boolean refresh) {
        slyceMessagingFragment.updateMessage(message, refresh);
    }

    @Override
    public void updateUI() {
        initializeChatFragment();
        chatPresenter.fetchChatMessages();
    }

    @Override
    public void setChatTitle(String displayName) {
        super.setTitle(displayName);
    }

    @Override
    public void requestLoadMoreMessages() {
        chatPresenter.requestLoadMoreMessages();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeChatFragment() {
        PublicProfile currentUserProfile = chatPresenter.getCurrentPublicProfile();
        if (currentUserProfile == null)
            return;

        slyceMessagingFragment.setStyle(R.style.ChatFragmentStyle);

        slyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.fragment_for_slyce_messaging);
        slyceMessagingFragment.setDefaultAvatarUrl(currentUserProfile.getAvatarUrl());
        slyceMessagingFragment.setDefaultDisplayName(currentUserProfile.getDisplayName());

        slyceMessagingFragment.setOnSendMessageListener(this);
        slyceMessagingFragment.setLoadMoreMessagesListener(this);
    }
}
