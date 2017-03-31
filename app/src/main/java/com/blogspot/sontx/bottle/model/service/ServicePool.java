package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;

public interface ServicePool {
    void initialize(Context context);

    ChannelService getChannelService();

    ChatService getChatService();

    PrivateProfileService getPrivateProfileService();

    PublicProfileService getPublicProfileService();
}
