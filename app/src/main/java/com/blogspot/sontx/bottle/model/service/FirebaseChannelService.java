package com.blogspot.sontx.bottle.model.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.utils.DateTimeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

class FirebaseChannelService extends FirebaseServiceBase implements ChannelService {
    private final DatabaseReference userChannelRef;
    private final DatabaseReference channelDetailRef;
    private final DatabaseReference channelMemberRef;

    FirebaseChannelService(Context context) {
        super(context);
        String userChannelKey = System.getProperty(Constants.FIREBASE_USER_CHANNEL_KEY);
        userChannelRef = FirebaseDatabase.getInstance().getReference(userChannelKey);

        String channelDetailKey = System.getProperty(Constants.FIREBASE_CHANNEL_DETAIL_KEY);
        channelDetailRef = FirebaseDatabase.getInstance().getReference(channelDetailKey);

        String channelMemberKey = System.getProperty(Constants.FIREBASE_CHANNEL_MEMBER_KEY);
        channelMemberRef = FirebaseDatabase.getInstance().getReference(channelMemberKey);
    }

    @Override
    public void getCurrentChannelsAsync(String currentUserId, final Callback<List<Channel>> callback) {
        userChannelRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Channel> channels = new ArrayList<>();
                if (dataSnapshot != null && dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Channel channel = snapshot.getValue(Channel.class);
                        channels.add(channel);
                    }
                }
                callback.onSuccess(channels);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void getChannelDetailAsync(String channelId, final Callback<ChannelDetail> callback) {
        channelDetailRef.child(channelId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.exists()) {
                    ChannelDetail detail = dataSnapshot.getValue(ChannelDetail.class);
                    callback.onSuccess(detail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void getChannelMembersAsync(String channelId, final Callback<List<ChannelMember>> callback) {
        channelMemberRef.child(channelId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChannelMember> members = new ArrayList<>();
                if (dataSnapshot != null && dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChannelMember member = snapshot.getValue(ChannelMember.class);
                        members.add(member);
                    }
                }
                callback.onSuccess(members);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public Channel createChannel(String currentUserId, String anotherMemberId) {
        Channel channel = createUserChannel(currentUserId);

        ChannelDetail detail = createChannelDetail(channel);
        channel.setDetail(detail);

        List<ChannelMember> members = createChannelMember(currentUserId, anotherMemberId, channel);
        channel.setMemberList(members);

        return channel;
    }

    @NonNull
    private List<ChannelMember> createChannelMember(String currentUserId, String anotherMemberId, Channel channel) {
        ChannelMember member1 = new ChannelMember();
        member1.setId(currentUserId);
        member1.setTimestamp(DateTimeUtils.utc());
        channelMemberRef.child(channel.getId()).push().setValue(member1);

        ChannelMember member2 = new ChannelMember();
        member2.setId(anotherMemberId);
        member2.setTimestamp(DateTimeUtils.utc());
        channelMemberRef.child(channel.getId()).push().setValue(member2);

        List<ChannelMember> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);
        return members;
    }

    @NonNull
    private ChannelDetail createChannelDetail(Channel channel) {
        ChannelDetail detail = new ChannelDetail();
        detail.setLastMessage(null);
        detail.setMessageType(ChatMessage.TYPE_NONE);
        detail.setTimestamp(DateTimeUtils.utc());

        channelDetailRef.child(channel.getId()).setValue(detail);
        return detail;
    }

    @NonNull
    private Channel createUserChannel(String currentUserId) {
        DatabaseReference currentUserChannelRef = userChannelRef.child(currentUserId).push();
        String channelId = currentUserChannelRef.getKey();

        Channel channel = new Channel();
        channel.setId(channelId);
        channel.setTimestamp(DateTimeUtils.utc());

        currentUserChannelRef.setValue(channel);
        return channel;
    }
}
