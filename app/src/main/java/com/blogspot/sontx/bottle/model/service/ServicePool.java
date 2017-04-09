package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.BottleCategoryService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.LoginService;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;

public interface ServicePool {
    void initialize(Context context);

    BottleServerAuthService getBottleServerAuthService();

    BottleCategoryService getBottleCategoryService();

    LoginService getLoginService();

    ChannelService getChannelService();

    ChatService getChatService();

    PrivateProfileService getPrivateProfileService();

    PublicProfileService getPublicProfileService();
}
