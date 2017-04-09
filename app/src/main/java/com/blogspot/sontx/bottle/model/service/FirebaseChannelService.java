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
import com.blogspot.sontx.bottle.utils.DateTimeUtils;
import com.blogspot.sontx.bottle.utils.ThreadUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public void cacheChannelsAsync(final Callback<List<Channel>> callback) {
        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();
        final String currentUserId = bottleUser.getUid();

        userChannelRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final boolean[] childThreadResults = {true};

                final boolean hasChannel = dataSnapshot != null && dataSnapshot.exists();

                if (hasChannel) {
                    publicProfileService.getPublicProfileAsync(currentUserId, new Callback<PublicProfile>() {
                        @Override
                        public void onSuccess(final PublicProfile result) {
                            ThreadUtils.run(new Runnable() {
                                @Override
                                public void run() {
                                    collectChannelInfo(dataSnapshot, childThreadResults, result);
                                    if (childThreadResults[0])
                                        callback.onSuccess(cachedChannels);
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable what) {
                            callback.onError(what);
                        }
                    });

                } else {
                    callback.onSuccess(cachedChannels = new ArrayList<>());
                }
            }

            private void collectChannelInfo(DataSnapshot dataSnapshot, final boolean[] childThreadResults, final PublicProfile currentUserProfile) {
                final CountDownLatch countDownLatch = new CountDownLatch((int) (dataSnapshot.getChildrenCount() * 3));// detail + members + 1 another public profile

                cachedChannels = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Channel channel = snapshot.getValue(Channel.class);
                    cachedChannels.add(channel);

                    getChannelDetailAsync(channel.getId(), new Callback<ChannelDetail>() {
                        @Override
                        public void onSuccess(ChannelDetail result) {
                            channel.setDetail(result);
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onError(Throwable what) {
                            callback.onError(what);
                            childThreadResults[0] = false;
                            countDownLatch.countDown();
                        }
                    });

                    getChannelMembersAsync(channel.getId(), new Callback<List<ChannelMember>>() {
                        @Override
                        public void onSuccess(List<ChannelMember> result) {
                            channel.setMemberList(result);

                            final Members members = new Members().analysis(channel);
                            members.currentUser.setPublicProfile(currentUserProfile);
                            publicProfileService.getPublicProfileAsync(members.anotherGuy.getId(), new Callback<PublicProfile>() {
                                @Override
                                public void onSuccess(PublicProfile result) {
                                    members.anotherGuy.setPublicProfile(result);
                                    countDownLatch.countDown();
                                }

                                @Override
                                public void onError(Throwable what) {
                                    callback.onError(what);
                                    childThreadResults[0] = false;
                                    countDownLatch.countDown();
                                }
                            });
                            countDownLatch.countDown();

                        }

                        @Override
                        public void onError(Throwable what) {
                            callback.onError(what);
                            childThreadResults[0] = false;
                            countDownLatch.countDown();
                        }
                    });
                }

                try {
                    countDownLatch.await(60, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Log.d(TAG, e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }

            class Members {
                ChannelMember currentUser;
                ChannelMember anotherGuy;

                Members analysis(Channel channel) {
                    List<ChannelMember> memberList = channel.getMemberList();
                    if (memberList.get(0).getId().equalsIgnoreCase(currentUserId)) {
                        currentUser = memberList.get(0);
                        anotherGuy = memberList.get(1);
                    } else {
                        currentUser = memberList.get(1);
                        anotherGuy = memberList.get(0);
                    }
                    return this;
                }
            }
        });
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
}
