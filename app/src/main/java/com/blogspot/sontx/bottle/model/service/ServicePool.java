package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerCategoryService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerUserSettingService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatServerLoginService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;

public interface ServicePool {
    void initialize(Context context);

    BottleServerAuthService getBottleServerAuthService();

    BottleServerCategoryService getBottleServerCategoryService();

    BottleServerRoomService getBottleServerRoomService();

    BottleServerMessageService getBottleServerMessageService();

    BottleServerUserSettingService getBottleServerUserSettingService();

    ChatServerLoginService getChatServerLoginService();

    ChannelService getChannelService();

    ChatService getChatService();

    PrivateProfileService getPrivateProfileService();

    PublicProfileService getPublicProfileService();
}
