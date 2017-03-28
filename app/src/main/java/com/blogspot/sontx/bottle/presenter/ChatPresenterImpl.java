package com.blogspot.sontx.bottle.presenter;

import android.net.Uri;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.ChatMessage;
import com.blogspot.sontx.bottle.model.bean.ChatMessageType;
import com.blogspot.sontx.bottle.model.service.FirebasePrivateProfileService;
import com.blogspot.sontx.bottle.model.service.FirebaseChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
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
    private final PrivateProfileService privateProfileService;
    private PublicProfile recipientPublicProfile;
    private PublicProfile currentPublicProfile;
    @Setter
    private String channelKey;

    public ChatPresenterImpl(ChatView chatView) {
        this.chatView = chatView;
        chatService = new FirebaseChatService(chatView.getContext());
        privateProfileService = new FirebasePrivateProfileService(chatView.getContext());

        recipientPublicProfile = new PublicProfile();
        recipientPublicProfile.setDisplayName(getDefaultDisplayName());
        recipientPublicProfile.setAvatarUrl(getDefaultAvatarUrl());

        currentPublicProfile = new PublicProfile();
        currentPublicProfile.setDisplayName(getDefaultDisplayName());
        currentPublicProfile.setAvatarUrl(getDefaultAvatarUrl());

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
        recipientPublicProfile.setId(id);
    }

    @Override
    public void setCurrentUserId(String id) {
        currentPublicProfile.setId(id);
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
        chatService.setup(channelKey, currentPublicProfile.getId(), recipientPublicProfile.getId());

//        privateProfileService.resolveAsync(currentPublicProfile.getId(), new Callback<PublicProfile>() {
//            @Override
//            public void onSuccess(PublicProfile result) {
//                currentPublicProfile = result;
//            }
//
//            @Override
//            public void onError(Throwable what) {
//                chatView.showErrorMessage(what.getMessage());
//            }
//        });
//        privateProfileService.resolveAsync(recipientPublicProfile.getId(), new Callback<PublicProfile>() {
//            @Override
//            public void onSuccess(PublicProfile result) {
//                recipientPublicProfile = result;
//            }
//
//            @Override
//            public void onError(Throwable what) {
//                chatView.showErrorMessage(what.getMessage());
//            }
//        });
    }

    private void addNewMessage(ChatMessage value) {
        if (value.getType() == null)
            return;

        Message message = null;
        if (value.getType().equalsIgnoreCase(ChatMessageType.TEXT.getType())) {
            TextMessage textMessage = new TextMessage();
            textMessage.setText(value.getContent());
            message = textMessage;
        } else if (value.getType().equalsIgnoreCase(ChatMessageType.MEDIA.getType())) {
            MediaMessage mediaMessage = new MediaMessage();
            mediaMessage.setUrl(value.getContent());
            message = mediaMessage;
        }

        if (message != null) {
            message.setDate(value.getCreatedTime());
            message.setSource(value.getSenderId().equalsIgnoreCase(currentPublicProfile.getId()) ? MessageSource.LOCAL_USER : MessageSource.EXTERNAL_USER);
            message.setUserId(value.getSenderId());

            PublicProfile senderPublicProfile = getAccountBasicInfoById(value.getSenderId());
            if (senderPublicProfile != null) {
                message.setDisplayName(senderPublicProfile.getDisplayName());
                message.setAvatarUrl(senderPublicProfile.getAvatarUrl());

                chatView.displayMessage(message);
            }
        }
    }

    private PublicProfile getAccountBasicInfoById(String id) {
        if (currentPublicProfile.getId().equalsIgnoreCase(id))
            return currentPublicProfile;
        if (recipientPublicProfile.getId().equalsIgnoreCase(id))
            return recipientPublicProfile;
        return null;
    }
}
