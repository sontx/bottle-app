package com.blogspot.sontx.bottle.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.presenter.ChatPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.ChatPresenter;
import com.blogspot.sontx.bottle.system.event.NotifyRegisterMessageEvent;
import com.blogspot.sontx.bottle.view.interfaces.ChatView;
import com.vanniktech.emoji.EmojiPopup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.ButterKnife;
import it.slyce.messaging.SlyceMessagingFragment;
import it.slyce.messaging.listeners.LoadMoreMessagesListener;
import it.slyce.messaging.listeners.UserSendsMessageListener;
import it.slyce.messaging.message.Message;
import it.slyce.messaging.view.text.FontEditText;

public class ChatActivity extends ActivityBase implements
        ChatView,
        UserSendsMessageListener,
        LoadMoreMessagesListener,
        FontEditText.OnBackPressedListener {

    static final String CHANNEL_KEY = "channel";
    static final String CHANNEL_ID_KEY = "channel-id";
    static final String ANOTHER_GUY_ID_KEY = "another-guy-id";

    private ChatPresenter chatPresenter;
    private SlyceMessagingFragment slyceMessagingFragment;
    private boolean isSoftKeyboardShowing = true;
    private EmojiPopup emojiPopup;

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

            initializeChatFragment();

            if (intent.hasExtra(CHANNEL_KEY)) {
                Channel channel = (Channel) intent.getSerializableExtra(CHANNEL_KEY);
                chatPresenter.setChannel(channel);
            } else if (intent.hasExtra(CHANNEL_ID_KEY)) {
                String channelId = intent.getStringExtra(CHANNEL_ID_KEY);
                chatPresenter.setChannelId(channelId);
            } else if (intent.hasExtra(ANOTHER_GUY_ID_KEY)) {
                String anotherGuyId = intent.getStringExtra(ANOTHER_GUY_ID_KEY);
                chatPresenter.chatWith(anotherGuyId);
            } else {
                finish();
                return;
            }

        } else {
            finish();
            return;
        }

        NotifyRegisterMessageEvent notifyRegisterMessageEvent = new NotifyRegisterMessageEvent();
        EventBus.getDefault().post(notifyRegisterMessageEvent);
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
        if (emojiPopup != null)
            emojiPopup.dismiss();
        chatPresenter.unregisterListeners();
    }

    @Override
    public void onBackPressed() {
        if (emojiPopup != null && emojiPopup.isShowing())
            emojiPopup.dismiss();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onBackPressed(View view) {
        if (emojiPopup != null && emojiPopup.isShowing()) {
            setInputType(true);
            return true;
        }
        return false;
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
    public void onShowEmojiKeyboard(View view) {
        setInputType(!isSoftKeyboardShowing);
    }

    @Override
    public void addNewMessage(final Message result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                slyceMessagingFragment.addNewMessage(result);
            }
        });
    }

    @Override
    public void onHasMoreMessages(List<Message> messages) {
        slyceMessagingFragment.loadMoreMessages(messages);
    }

    @Override
    public void updateChatMessage(final Message message, final boolean refresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                slyceMessagingFragment.updateMessage(message, refresh);
            }
        });
    }

    @Override
    public void setChatTitle(String displayName) {
        super.setTitle(displayName);
    }

    @Override
    public void closeAfterDeletedChannel(Channel channel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Channel is expired");
        builder.setMessage("This chat channel is no longer available because it's inactived over 24H.");
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void requestLoadMoreMessages() {
        chatPresenter.requestLoadMoreMessages();
    }

    private void showSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        FontEditText entryField = slyceMessagingFragment.getEntryField();
        inputMethodManager.showSoftInput(entryField, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setInputType(boolean isSoftKeyboardShowing) {
        this.isSoftKeyboardShowing = isSoftKeyboardShowing;

        if (isSoftKeyboardShowing) {
            slyceMessagingFragment.setEmojiButtonToggleState(false);
            emojiPopup.dismiss();
            showSoftKeyboard();
        } else {
            slyceMessagingFragment.setEmojiButtonToggleState(true);
            showSoftKeyboard();
            emojiPopup.toggle();
        }
    }

    private void setupEmoji() {
        View rootView = ButterKnife.findById(this, R.id.root_view);
        FontEditText entryField = slyceMessagingFragment.getEntryField();
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(entryField);
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

        slyceMessagingFragment.setStyle(R.style.ChatFragmentStyle);

        slyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.fragment_for_slyce_messaging);
        slyceMessagingFragment.setDefaultAvatarUrl(currentUserProfile.getAvatarUrl());
        slyceMessagingFragment.setDefaultDisplayName(currentUserProfile.getDisplayName());

        slyceMessagingFragment.setOnSendMessageListener(this);
        slyceMessagingFragment.setLoadMoreMessagesListener(this);

        FontEditText entryField = slyceMessagingFragment.getEntryField();
        entryField.setOnBackPressedListener(this);

        slyceMessagingFragment.setEmojiButtonToggleState(false);

        setupEmoji();
    }
}
