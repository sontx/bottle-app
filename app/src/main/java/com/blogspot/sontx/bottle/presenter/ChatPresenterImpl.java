package com.blogspot.sontx.bottle.presenter;

import android.net.Uri;

import com.blogspot.sontx.bottle.model.Constants;
import com.blogspot.sontx.bottle.model.bean.AccountBasicInfo;
import com.blogspot.sontx.bottle.model.bean.ChatMessage;
import com.blogspot.sontx.bottle.model.bean.MessageType;
import com.blogspot.sontx.bottle.model.service.FirebaseAccountManagerService;
import com.blogspot.sontx.bottle.model.service.FirebaseChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.AccountManagerService;
import com.blogspot.sontx.bottle.model.service.interfaces.Callback;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.SimpleCallback;
import com.blogspot.sontx.bottle.presenter.interfaces.ChatPresenter;
import com.blogspot.sontx.bottle.view.interfaces.ChatView;

import java.util.List;

import it.slyce.messaging.message.MediaMessage;
import it.slyce.messaging.message.Message;
import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;
import lombok.Setter;

public class ChatPresenterImpl extends PresenterBase implements ChatPresenter {
    private final ChatView chatView;
    private final ChatService chatService;
    private final AccountManagerService accountManagerService;
    private AccountBasicInfo recipientAccountBasicInfo;
    private AccountBasicInfo currentAccountBasicInfo;
    @Setter
    private String channelKey;

    public ChatPresenterImpl(ChatView chatView) {
        this.chatView = chatView;
        chatService = new FirebaseChatService(chatView.getContext());
        accountManagerService = new FirebaseAccountManagerService(chatView.getContext());

        recipientAccountBasicInfo = new AccountBasicInfo();
        recipientAccountBasicInfo.setDisplayName(getDefaultDisplayName());
        recipientAccountBasicInfo.setAvatarUrl(getDefaultAvatarUrl());

        currentAccountBasicInfo = new AccountBasicInfo();
        currentAccountBasicInfo.setDisplayName(getDefaultDisplayName());
        currentAccountBasicInfo.setAvatarUrl(getDefaultAvatarUrl());

        chatService.setOnNewChatMessage(new SimpleCallback<ChatMessage>() {
            @Override
            public void onCallback(ChatMessage value) {
                addNewMessage(value);
            }
        });
    }

    @Override
    public String getDefaultAvatarUrl() {
        return System.getProperty(Constants.UI_DEFAULT_AVATAR_URL_KEY);
    }

    @Override
    public String getDefaultDisplayName() {
        return System.getProperty(Constants.UI_DEFAULT_DISPLAY_NAME_KEY);
    }

    @Override
    public void sendAsync(String text) {
        chatService.sendAsync(text, new Callback<ChatMessage>() {
            @Override
            public void onSuccess(ChatMessage result) {
                // chatView.displayMessage(result);
            }

            @Override
            public void onError(Throwable what) {
                chatView.showErrorMessage(what.getMessage());
            }
        });
    }

    @Override
    public void sendAsync(Uri imageUri) {
        chatService.sendAsync(imageUri, new Callback<ChatMessage>() {
            @Override
            public void onSuccess(ChatMessage result) {
                // chatView.displayMessage(result);
            }

            @Override
            public void onError(Throwable what) {
                chatView.showErrorMessage(what.getMessage());
            }
        });
    }

    @Override
    public void setRecipientUserId(String id) {
        recipientAccountBasicInfo.setId(id);
    }

    @Override
    public void setCurrentUserId(String id) {
        currentAccountBasicInfo.setId(id);
    }

    @Override
    public List<Message> getMoreMessages() {
        return chatService.getMoreMessages();
    }

    @Override
    public boolean isMoreMessagesExist() {
        return false;
    }

    @Override
    public void setup() {
        chatService.setup(channelKey, currentAccountBasicInfo.getId(), recipientAccountBasicInfo.getId());

        accountManagerService.resolveAsync(currentAccountBasicInfo.getId(), new Callback<AccountBasicInfo>() {
            @Override
            public void onSuccess(AccountBasicInfo result) {
                currentAccountBasicInfo = result;
            }

            @Override
            public void onError(Throwable what) {
                chatView.showErrorMessage(what.getMessage());
            }
        });
        accountManagerService.resolveAsync(recipientAccountBasicInfo.getId(), new Callback<AccountBasicInfo>() {
            @Override
            public void onSuccess(AccountBasicInfo result) {
                recipientAccountBasicInfo = result;
            }

            @Override
            public void onError(Throwable what) {
                chatView.showErrorMessage(what.getMessage());
            }
        });
    }

    private void addNewMessage(ChatMessage value) {
        if (value.getType() == null)
            return;

        Message message = null;
        if (value.getType().equalsIgnoreCase(MessageType.TEXT.getType())) {
            TextMessage textMessage = new TextMessage();
            textMessage.setText(value.getContent());
            message = textMessage;
        } else if (value.getType().equalsIgnoreCase(MessageType.MEDIA.getType())) {
            MediaMessage mediaMessage = new MediaMessage();
            mediaMessage.setUrl(value.getContent());
            message = mediaMessage;
        }

        if (message != null) {
            message.setDate(value.getCreatedTime());
            message.setSource(MessageSource.EXTERNAL_USER);
            message.setUserId(value.getSenderId());

            AccountBasicInfo senderAccountBasicInfo = getAccountBasicInfoById(value.getSenderId());
            if (senderAccountBasicInfo != null) {
                message.setDisplayName(senderAccountBasicInfo.getDisplayName());
                message.setAvatarUrl(senderAccountBasicInfo.getAvatarUrl());

                chatView.displayMessage(message);
            }
        }
    }

    private AccountBasicInfo getAccountBasicInfoById(String id) {
        if (currentAccountBasicInfo.getId().equalsIgnoreCase(id))
            return currentAccountBasicInfo;
        if (recipientAccountBasicInfo.getId().equalsIgnoreCase(id))
            return recipientAccountBasicInfo;
        return null;
    }
}
