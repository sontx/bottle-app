package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.AccountBasicInfo;
import com.blogspot.sontx.bottle.model.bean.ChatChannelInfo;
import com.blogspot.sontx.bottle.model.service.interfaces.AccountManagerService;
import com.blogspot.sontx.bottle.model.service.interfaces.Callback;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatChannelService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lombok.Data;
import lombok.Setter;

public class FirebaseChatChannelService extends FirebaseServiceBase implements ChatChannelService, ChildEventListener {
    private DatabaseReference userChatChannelRef;
    private String currentUserId;
    @Setter
    private Callback<ChatChannelInfo> onNewChatChannel;
    private final AccountManagerService accountManagerService;

    public FirebaseChatChannelService(Context context) {
        super(context);
        accountManagerService = new FirebaseAccountManagerService(context);
    }

    @Override
    public void setup(String userId) {
        this.currentUserId = userId;
        String userChatChannelKey = System.getProperty(Constants.FIREBASE_CHAT_USER_CHANNEL_KEY);
        userChatChannelRef = FirebaseDatabase.getInstance().getReference(userChatChannelKey).child(userId);
        userChatChannelRef.addChildEventListener(this);
    }

    @Override
    public void fetchAvailableChatChannelsAsync() {
        userChatChannelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    resolveChatChannelByKey(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (onNewChatChannel != null)
                    onNewChatChannel.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        resolveChatChannelByKey(dataSnapshot.getKey());
    }

    private void resolveChatChannelByKey(String channelKey) {
        String channelsKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_KEY);
        String channelInfoKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_KEY);

        FirebaseDatabase.getInstance().getReference(channelsKey).child(channelKey).child(channelInfoKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String createKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_CREATE_KEY);
                        String lastActiveKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_LAST_ACTIVE_KEY);
                        String user1IdKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_USER1_ID_KEY);
                        String user2IdKey = System.getProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_USER2_ID_KEY);

                        TempChatChannelInfo tempChatChannelInfo = new TempChatChannelInfo();

                        tempChatChannelInfo.setCreateTime(dataSnapshot.child(createKey).getValue(Long.class));
                        tempChatChannelInfo.setLastActiveTime(dataSnapshot.child(lastActiveKey).getValue(Long.class));
                        tempChatChannelInfo.setUser1Id(dataSnapshot.child(user1IdKey).getValue(String.class));
                        tempChatChannelInfo.setUser2Id(dataSnapshot.child(user2IdKey).getValue(String.class));

                        getDisplayChatChannelInfo(tempChatChannelInfo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (onNewChatChannel != null)
                            onNewChatChannel.onError(databaseError.toException());
                    }
                });
    }

    private void getDisplayChatChannelInfo(final TempChatChannelInfo tempChatChannelInfo) {
        String recipientUserId;
        if (tempChatChannelInfo.getUser1Id().equalsIgnoreCase(currentUserId))
            recipientUserId = tempChatChannelInfo.getUser2Id();
        else
            recipientUserId = tempChatChannelInfo.getUser1Id();

        accountManagerService.resolveAsync(recipientUserId, new Callback<AccountBasicInfo>() {
            @Override
            public void onSuccess(AccountBasicInfo accountBasicInfo) {
                ChatChannelInfo chatChannelInfo = new ChatChannelInfo();
                chatChannelInfo.setLastActiveTime(tempChatChannelInfo.getLastActiveTime());
                chatChannelInfo.setRecipientDisplayName(accountBasicInfo.getDisplayName());
                chatChannelInfo.setRecipientAvatarUrl(accountBasicInfo.getAvatarUrl());
                if (onNewChatChannel != null)
                    onNewChatChannel.onSuccess(chatChannelInfo);
            }

            @Override
            public void onError(Throwable what) {
                if (onNewChatChannel != null)
                    onNewChatChannel.onError(what);
            }
        });
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

    @Data
    private static class TempChatChannelInfo {
        private long createTime;
        private long lastActiveTime;
        private String user1Id;
        private String user2Id;
    }
}
