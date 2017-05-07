package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.BottleFileStreamService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerCategoryService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerGeoService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerStompService;
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

    BottleServerGeoService getBottleServerGeoService();

    BottleServerRoomService getBottleServerRoomService();

    BottleServerMessageService getBottleServerMessageService();

    BottleServerUserSettingService getBottleServerUserSettingService();

    BottleFileStreamService getBottleFileStreamService();

    BottleServerChatService getBottleServerChatService();

    BottleServerStompService getBottleServerStompService();

    ChatServerLoginService getChatServerLoginService();

    ChannelService getChannelService();

    ChatService getChatService();

    PrivateProfileService getPrivateProfileService();

    PublicProfileService getPublicProfileService();
}
