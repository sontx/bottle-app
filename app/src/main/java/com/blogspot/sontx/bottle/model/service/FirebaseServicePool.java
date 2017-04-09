package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatServerLoginService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.google.firebase.database.FirebaseDatabase;

import lombok.Getter;

public final class FirebaseServicePool extends ServicePoolBase {
    private static ServicePool instance = null;
    @Getter
    private ChatServerLoginService chatServerLoginService;
    @Getter
    private ChannelService channelService;
    @Getter
    private ChatService chatService;
    @Getter
    private PrivateProfileService privateProfileService;
    @Getter
    private PublicProfileService publicProfileService;

    private FirebaseServicePool() {
    }

    public static ServicePool getInstance() {
        if (instance == null)
            instance = new FirebaseServicePool();
        return instance;
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        chatServerLoginService = new FirebaseChatServerLoginService(context);
        privateProfileService = new FirebasePrivateProfileService(context);
        publicProfileService = new FirebasePublicProfileService(context);
        chatService = new FirebaseChatService(context);
        channelService = new FirebaseChannelService(context, publicProfileService);
    }
}
