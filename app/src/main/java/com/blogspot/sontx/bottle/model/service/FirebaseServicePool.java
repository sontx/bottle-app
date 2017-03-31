package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;

import lombok.Getter;

public final class FirebaseServicePool implements ServicePool {
    private static ServicePool instance = null;
    @Getter
    private ChannelService channelService;
    @Getter
    private ChatService chatService;
    @Getter
    private PrivateProfileService privateProfileService;
    @Getter
    private PublicProfileService publicProfileService;

    FirebaseServicePool() {
    }

    public static ServicePool getInstance() {
        if (instance == null)
            instance = new FirebaseServicePool();
        return instance;
    }

    @Override
    public void initialize(Context context) {
        channelService = new FirebaseChannelService(context);
        chatService = new FirebaseChatService(context);
        privateProfileService = new FirebasePrivateProfileService(context);
        publicProfileService = new FirebasePublicProfileService(context);
    }
}
