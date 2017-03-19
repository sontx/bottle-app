package com.blogspot.sontx.bottle.model.service;

import android.net.Uri;

import com.blogspot.sontx.bottle.model.Constants;
import com.blogspot.sontx.bottle.model.bean.ChatMessage;
import com.blogspot.sontx.bottle.model.bean.MessageType;
import com.blogspot.sontx.bottle.model.service.interfaces.Callback;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.SimpleCallback;
import com.blogspot.sontx.bottle.utils.DateTimeUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import it.slyce.messaging.message.Message;
import lombok.Setter;

public class FirebaseChatService implements ChatService, ChildEventListener {
    private DatabaseReference messagesRef;
    private DatabaseReference infoRef;
    @Setter
    private String currentUserId;
    @Setter
    private SimpleCallback<ChatMessage> onNewChatMessage;

    @Override
    public void sendAsync(String text, Callback<ChatMessage> callback) {
        sendAsync(text, MessageType.TEXT.getType(), callback);
    }

    @Override
    public void sendAsync(Uri imageUri, Callback<ChatMessage> callback) {
        sendAsync(imageUri.toString(), MessageType.MEDIA.getType(), callback);
    }

    @Override
    public List<Message> getMoreMessages() {
        return new ArrayList<>();
    }

    @Override
    public void setup(String channelKey, String user1, String user2) {
        String rootChannelKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_KEY);
        DatabaseReference channelRef = FirebaseDatabase.getInstance().getReference(rootChannelKey);

        String infoKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_KEY);
        infoRef = channelRef.child(infoKey);
        String user1IdKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_USER1_ID_KEY);
        infoRef.child(user1IdKey).setValue(user1);
        String user2IdKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_USER2_ID_KEY);
        infoRef.child(user2IdKey).setValue(user2);

        String messagesKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_MESSAGES_KEY);
        messagesRef = channelRef.child(messagesKey);

        messagesRef.addChildEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (onNewChatMessage != null)
            onNewChatMessage.onCallback(dataSnapshot.getValue(ChatMessage.class));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }

    private void sendAsync(String content, String type, final Callback<ChatMessage> callback) {
        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setCreatedTime(DateTimeUtils.utc());
        chatMessage.setSenderId(currentUserId);
        chatMessage.setType(type);
        messagesRef.push().setValue(chatMessage, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    callback.onError(databaseError.toException());
                } else {
                    String lastActiveKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_LAST_ACTIVE_KEY);
                    infoRef.child(lastActiveKey).setValue(DateTimeUtils.utc());
                    callback.onSuccess(chatMessage);
                }
            }
        });
    }
}
