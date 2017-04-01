package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.utils.DateTimeUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Enumeration;
import java.util.Hashtable;

import lombok.Setter;

class FirebaseChatService extends FirebaseServiceBase implements ChatService {
    private DatabaseReference messagesRef;
    private DatabaseReference detailsRef;
    private Hashtable<String, DatabaseRefWrapper> registerChannelsRef;

    @Setter
    private String currentUserId;
    @Setter
    private SimpleCallback<ChatMessage> onNewChatMessage;
    @Setter
    private SimpleCallback<ChatMessage> onChatMessageChanged;

    FirebaseChatService(Context context) {
        super(context);
        String messagesKey = System.getProperty(Constants.FIREBASE_MESSAGES_KEY);
        messagesRef = FirebaseDatabase.getInstance().getReference(messagesKey);

        String channelDetailsKey = System.getProperty(Constants.FIREBASE_CHANNEL_DETAIL_KEY);
        detailsRef = FirebaseDatabase.getInstance().getReference(channelDetailsKey);

        registerChannelsRef = new Hashtable<>();
    }

    @Override
    public void registerChannel(final String channelId) {
        if (registerChannelsRef.containsKey(channelId))
            return;

        DatabaseReference registerChannelRef = messagesRef.child(channelId);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (onNewChatMessage != null) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    chatMessage.setChannelId(channelId);
                    onNewChatMessage.onCallback(chatMessage);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (onChatMessageChanged != null) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    chatMessage.setChannelId(channelId);
                    onChatMessageChanged.onCallback(chatMessage);
                }
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
        };

        registerChannelRef.orderByChild("timestamp").startAt(DateTimeUtils.utc()).addChildEventListener(childEventListener);

        DatabaseRefWrapper databaseRefWrapper = new DatabaseRefWrapper();
        databaseRefWrapper.childEventListener = childEventListener;
        databaseRefWrapper.databaseReference = registerChannelRef;

        registerChannelsRef.put(channelId, databaseRefWrapper);
    }

    @Override
    public void unregisterChannel(String channelId) {
        DatabaseRefWrapper databaseRefWrapper = registerChannelsRef.remove(channelId);
        if (databaseRefWrapper != null) {
            databaseRefWrapper.databaseReference.removeEventListener(databaseRefWrapper.childEventListener);
        }
    }

    @Override
    public void unregisterAllChannels() {
        Enumeration<DatabaseRefWrapper> elements = registerChannelsRef.elements();
        while (elements.hasMoreElements()) {
            DatabaseRefWrapper databaseRefWrapper = elements.nextElement();
            databaseRefWrapper.databaseReference.removeEventListener(databaseRefWrapper.childEventListener);
        }
    }

    @Override
    public void sendAsync(String channelId, String text, Callback<ChatMessage> callback) {
        sendAsync(channelId, text, ChatMessage.TYPE_TEXT, callback);
    }

    private void sendAsync(final String channelId, String content, String type, final Callback<ChatMessage> callback) {
        final ChatMessage chatMessage = new ChatMessage();

        chatMessage.setMessage(content);
        chatMessage.setTimestamp(DateTimeUtils.utc());
        chatMessage.setSenderId(currentUserId);
        chatMessage.setMessageType(type);

        messagesRef.child(channelId).push().setValue(chatMessage, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    callback.onError(databaseError.toException());
                } else {
                    ChannelDetail detail = new ChannelDetail();
                    detail.setMessageType(chatMessage.getMessageType());
                    detail.setLastMessage(chatMessage.getMessage());
                    detail.setTimestamp(chatMessage.getTimestamp());
                    detailsRef.child(channelId).setValue(detail);
                    callback.onSuccess(chatMessage);
                }
            }
        });
    }

    private static class DatabaseRefWrapper {
        private DatabaseReference databaseReference;
        private ChildEventListener childEventListener;
    }
}
