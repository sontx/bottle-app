package com.blogspot.sontx.bottle.model.service;

import android.content.Context;
import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.system.event.ChatChannelChangedEvent;
import com.blogspot.sontx.bottle.utils.BeanUtils;
import com.blogspot.sontx.bottle.utils.DateTimeUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import lombok.Setter;

public class FirebaseChatService extends FirebaseServiceBase implements ChatService {
    private DatabaseReference messagesRef;
    private DatabaseReference detailsRef;
    private Hashtable<String, DatabaseRefWrapper> registerChannelsRef;

    @Setter
    private SimpleCallback<ChatMessage> onNewChatMessage;
    @Setter
    private SimpleCallback<ChatMessage> onChatMessageChanged;

    public FirebaseChatService(Context context) {
        super(context);
        String messagesKey = System.getProperty(Constants.FIREBASE_MESSAGES_KEY);
        messagesRef = FirebaseDatabase.getInstance().getReference(messagesKey);

        String channelDetailsKey = System.getProperty(Constants.FIREBASE_CHANNEL_DETAIL_KEY);
        detailsRef = FirebaseDatabase.getInstance().getReference(channelDetailsKey);

        registerChannelsRef = new Hashtable<>();
    }

    @Override
    public boolean isRegisteredChatMessageListeners() {
        return onNewChatMessage != null && onChatMessageChanged != null;
    }

    @Override
    public void clearCached() {
        super.clearCached();
        onNewChatMessage = null;
        onChatMessageChanged = null;
        unregisterAllChannels();
    }

    @Override
    public void getMoreMessages(final String channelId, long startAt, int limit, final Callback<List<ChatMessage>> callback) {
        Log.d(TAG, "getMoreMessages -> " + channelId);

        messagesRef.child(channelId).orderByChild("timestamp").endAt(startAt).limitToLast(limit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChatMessage> chatMessages = new ArrayList<>((int) dataSnapshot.getChildrenCount());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                    chatMessage.setId(snapshot.getKey());
                    chatMessage.setChannelId(channelId);
                    chatMessages.add(chatMessage);
                    updateReceivedMessageStateIfNecessary(snapshot.getRef(), chatMessage);
                }
                callback.onSuccess(chatMessages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void registerChannel(final Channel channel) {
        if (registerChannelsRef.containsKey(channel.getId()))
            return;

        Log.d(TAG, "registerChannel -> " + channel);

        ChildEventListener chatMessageAddedEvent = new ChildEventAdapter() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "added new message");
                if (onNewChatMessage != null) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    chatMessage.setChannelId(channel.getId());
                    chatMessage.setId(dataSnapshot.getKey());
                    onNewChatMessage.onCallback(chatMessage);
                    updateReceivedMessageStateIfNecessary(dataSnapshot.getRef(), chatMessage);
                }
            }
        };
        ChildEventListener chatMessageChangedEvent = new ChildEventAdapter() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "updated message");
                if (onChatMessageChanged != null) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    chatMessage.setChannelId(channel.getId());
                    chatMessage.setId(dataSnapshot.getKey());
                    onChatMessageChanged.onCallback(chatMessage);
                }
            }
        };
        ChildEventListener channelDetailChangedEvent = new ChildEventAdapter() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String name = dataSnapshot.getKey();
                Object value = dataSnapshot.getValue();
                BeanUtils.updateBean(name, value, channel.getDetail());

                ChatChannelChangedEvent chatChannelChangedEvent = new ChatChannelChangedEvent();
                chatChannelChangedEvent.setChannel(channel);
                EventBus.getDefault().post(chatChannelChangedEvent);
            }
        };

        DatabaseReference registerChannelRef = messagesRef.child(channel.getId());
        registerChannelRef.orderByChild("timestamp").startAt(DateTimeUtils.utc()).addChildEventListener(chatMessageAddedEvent);
        registerChannelRef.orderByChild("timestamp").addChildEventListener(chatMessageChangedEvent);

        DatabaseReference registerChannelDetailRef = detailsRef.child(channel.getId());
        registerChannelDetailRef.addChildEventListener(channelDetailChangedEvent);

        DatabaseRefWrapper databaseRefWrapper = new DatabaseRefWrapper();
        databaseRefWrapper.chatMessageAddedEvent = chatMessageAddedEvent;
        databaseRefWrapper.chatMessageChangedEvent = chatMessageChangedEvent;
        databaseRefWrapper.channelDetailChangedEvent = channelDetailChangedEvent;
        databaseRefWrapper.messageRef = registerChannelRef;
        databaseRefWrapper.channelDetailRef = registerChannelDetailRef;

        registerChannelsRef.put(channel.getId(), databaseRefWrapper);
    }

    @Override
    public void unregisterChannel(String channelId) {
        DatabaseRefWrapper databaseRefWrapper = registerChannelsRef.remove(channelId);
        if (databaseRefWrapper != null) {
            databaseRefWrapper.messageRef.removeEventListener(databaseRefWrapper.chatMessageAddedEvent);
            databaseRefWrapper.messageRef.removeEventListener(databaseRefWrapper.chatMessageChangedEvent);
            databaseRefWrapper.channelDetailRef.removeEventListener(databaseRefWrapper.channelDetailChangedEvent);
        }
    }

    @Override
    public void unregisterAllChannels() {
        Enumeration<DatabaseRefWrapper> elements = registerChannelsRef.elements();
        while (elements.hasMoreElements()) {
            DatabaseRefWrapper databaseRefWrapper = elements.nextElement();
            databaseRefWrapper.messageRef.removeEventListener(databaseRefWrapper.chatMessageAddedEvent);
            databaseRefWrapper.messageRef.removeEventListener(databaseRefWrapper.chatMessageChangedEvent);
            databaseRefWrapper.channelDetailRef.removeEventListener(databaseRefWrapper.channelDetailChangedEvent);
        }
    }

    @Override
    public void sendAsync(String channelId, String text, Callback<ChatMessage> callback) {
        sendAsync(channelId, text, ChatMessage.TYPE_TEXT, callback);
    }

    @Override
    public void updateChatMessageStateAsync(String channelId, String id, String newState) {
        messagesRef.child(channelId).child(id).child("state").setValue(newState);
    }

    private void sendAsync(final String channelId, String content, String type, final Callback<ChatMessage> callback) {
        final ChatMessage chatMessage = new ChatMessage();

        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();
        String currentUserId = bottleUser.getUid();

        chatMessage.setMessage(content);
        //chatMessage.setTimestamp(DateTimeUtils.utc());
        chatMessage.setSenderId(currentUserId);
        chatMessage.setMessageType(type);
        chatMessage.setState(ChatMessage.STATE_SENDING);

        Map<String, Object> objectMap = BeanUtils.toMap(chatMessage);
        objectMap.put("timestamp", ServerValue.TIMESTAMP);

        messagesRef.child(channelId).push().setValue(objectMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(final DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    callback.onError(databaseError.toException());
                } else {
                    databaseReference.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ChannelDetail detail = new ChannelDetail();
                            detail.setMessageType(chatMessage.getMessageType());
                            detail.setLastMessage(chatMessage.getMessage());
                            detail.setTimestamp(dataSnapshot.getValue(Long.class));
                            detailsRef.child(channelId).setValue(detail);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    chatMessage.setState(ChatMessage.STATE_SENT);
                    chatMessage.setId(databaseReference.getKey());
                    callback.onSuccess(chatMessage);

                    databaseReference.child("state").setValue(ChatMessage.STATE_SENT);
                }
            }
        });
    }

    private void updateReceivedMessageStateIfNecessary(DatabaseReference databaseReference, ChatMessage chatMessage) {
        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();
        String currentUserId = bottleUser.getUid();

        if (chatMessage.getSenderId() != null && !chatMessage.getSenderId().equalsIgnoreCase(currentUserId))
            databaseReference.child("state").setValue(ChatMessage.STATE_RECEIVED);
    }

    private static class DatabaseRefWrapper {
        private DatabaseReference messageRef;
        private DatabaseReference channelDetailRef;
        private ChildEventListener chatMessageAddedEvent;
        private ChildEventListener chatMessageChangedEvent;
        private ChildEventListener channelDetailChangedEvent;
    }
}
