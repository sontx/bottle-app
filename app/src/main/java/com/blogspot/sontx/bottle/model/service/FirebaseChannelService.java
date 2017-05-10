package com.blogspot.sontx.bottle.model.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.system.BottleContext;
import com.blogspot.sontx.bottle.system.event.ChatChannelAddedEvent;
import com.blogspot.sontx.bottle.system.event.ChatChannelRemovedEvent;
import com.blogspot.sontx.bottle.utils.DateTimeUtils;
import com.blogspot.sontx.bottle.utils.ThreadUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class FirebaseChannelService extends FirebaseServiceBase implements ChannelService {
    private final DatabaseReference userChannelRef;
    private final DatabaseReference channelDetailRef;
    private final DatabaseReference channelMemberRef;
    private List<Channel> cachedChannels;
    private PublicProfileService publicProfileService;
    private ChildEventAdapter childEventListener;
    private DatabaseReference currentUserRef;
    private String currentUserId;

    FirebaseChannelService(Context context, PublicProfileService publicProfileService) {
        super(context);
        this.publicProfileService = publicProfileService;

        String userChannelKey = System.getProperty(Constants.FIREBASE_USER_CHANNEL_KEY);
        userChannelRef = FirebaseDatabase.getInstance().getReference(userChannelKey);

        String channelDetailKey = System.getProperty(Constants.FIREBASE_CHANNEL_DETAIL_KEY);
        channelDetailRef = FirebaseDatabase.getInstance().getReference(channelDetailKey);

        String channelMemberKey = System.getProperty(Constants.FIREBASE_CHANNEL_MEMBER_KEY);
        channelMemberRef = FirebaseDatabase.getInstance().getReference(channelMemberKey);

    }

    @Override
    public void clearCached() {
        super.clearCached();
        cachedChannels = null;
    }

    @Override
    public void getCurrentChannelsAsync(final Callback<List<Channel>> callback) {
        if (cachedChannels != null) {
            callback.onSuccess(cachedChannels);
            return;
        }

        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();
        String currentUserId = bottleUser.getUid();

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
    public Channel createChannel(String anotherMemberId) {
        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();
        String currentUserId = bottleUser.getUid();

        Channel channel = createUserChannel(currentUserId);

        ChannelDetail detail = createChannelDetail(channel);
        channel.setDetail(detail);

        List<ChannelMember> members = createChannelMember(currentUserId, anotherMemberId, channel);
        channel.setMemberList(members);

        return channel;
    }

    @Override
    public void registerChannelEvents() {
        unregisterChannelEvents();

        BottleContext bottleContext = App.getInstance().getBottleContext();
        BottleUser bottleUser = bottleContext.getCurrentBottleUser();
        String currentUserId = bottleUser.getUid();

        if (this.currentUserId == null || !this.currentUserId.equals(currentUserId)) {
            this.currentUserId = currentUserId;
            currentUserRef = userChannelRef.child(currentUserId);
        }

        cachedChannels = new ArrayList<>();
        PublicProfile currentPublicProfile = bottleContext.getCurrentPublicProfile();
        childEventListener = getChildEventListener(currentPublicProfile);
        currentUserRef.addChildEventListener(childEventListener);
    }

    @Override
    public void unregisterChannelEvents() {
        if (childEventListener != null && userChannelRef != null) {
            userChannelRef.removeEventListener(childEventListener);
            childEventListener = null;
            cachedChannels = null;
        }
    }

    @Override
    public boolean isCachedChannels() {
        return cachedChannels != null;
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

    @NonNull
    private ChildEventAdapter getChildEventListener(final PublicProfile currentPublicProfile) {
        return new ChildEventAdapter() {
            @Override
            public synchronized void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                ThreadUtils.run(new Runnable() {
                    @Override
                    public void run() {
                        collectChannelInfo(dataSnapshot);
                    }
                });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String channelId = dataSnapshot.getKey();
                synchronized (this) {
                    if (cachedChannels != null) {
                        for (int i = 0; i < cachedChannels.size(); i++) {
                            Channel cachedChannel = cachedChannels.get(i);
                            if (cachedChannel.getId().equals(channelId)) {
                                cachedChannels.remove(i);
                                break;
                            }
                        }
                    }
                    broadcastRemoveChannelEvent(channelId);
                }
            }

            private void collectChannelInfo(DataSnapshot dataSnapshot) {
                final CountDownLatch countDownLatch = new CountDownLatch(3);// detail + members + 1 another public profile

                final Channel channel = dataSnapshot.getValue(Channel.class);

                getChannelDetailAsync(channel.getId(), new Callback<ChannelDetail>() {
                    @Override
                    public void onSuccess(ChannelDetail result) {
                        channel.setDetail(result);
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(Throwable what) {
                        Log.e(TAG, what.getMessage());
                        countDownLatch.countDown();
                    }
                });

                getChannelMembersAsync(channel.getId(), new Callback<List<ChannelMember>>() {
                    @Override
                    public void onSuccess(List<ChannelMember> result) {
                        channel.setMemberList(result);

                        ChannelMember currentUser = channel.getCurrentUser();
                        if (currentUser != null)
                            currentUser.setPublicProfile(currentPublicProfile);

                        final ChannelMember anotherGuy = channel.getAnotherGuy();

                        if (anotherGuy != null) {
                            publicProfileService.getPublicProfileAsync(anotherGuy.getId(), new Callback<PublicProfile>() {
                                @Override
                                public void onSuccess(PublicProfile result) {
                                    anotherGuy.setPublicProfile(result);
                                    countDownLatch.countDown();
                                }

                                @Override
                                public void onError(Throwable what) {
                                    Log.e(TAG, what.getMessage());
                                    countDownLatch.countDown();
                                }
                            });
                        } else {
                            countDownLatch.countDown();
                        }

                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(Throwable what) {
                        Log.e(TAG, what.getMessage());
                        countDownLatch.countDown();
                    }
                });

                try {
                    countDownLatch.await(10, TimeUnit.SECONDS);
                    if (channel.isValid()) {
                        synchronized (this) {
                            cachedChannels.add(channel);
                        }
                        broadcastAddChannelEvent(channel);
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        };
    }

    private void broadcastAddChannelEvent(Channel channel) {
        ChatChannelAddedEvent chatChannelAddedEvent = new ChatChannelAddedEvent();
        chatChannelAddedEvent.setChannel(channel);
        EventBus.getDefault().post(chatChannelAddedEvent);
    }

    private void broadcastRemoveChannelEvent(String channelId) {
        ChatChannelRemovedEvent chatChannelRemovedEvent = new ChatChannelRemovedEvent();
        chatChannelRemovedEvent.setChannelId(channelId);
        EventBus.getDefault().post(chatChannelRemovedEvent);
    }
}
