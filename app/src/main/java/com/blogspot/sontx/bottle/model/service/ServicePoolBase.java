package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.BottleFileStreamService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerCategoryService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerChatService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerGeoService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerProfileService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerStompService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerUserSettingService;

import lombok.Getter;

abstract class ServicePoolBase implements ServicePool {
    @Getter
    private BottleServerAuthService bottleServerAuthService;

    @Getter
    private BottleServerCategoryService bottleServerCategoryService;

    @Getter
    private BottleServerGeoService bottleServerGeoService;

    @Getter
    private BottleServerRoomService bottleServerRoomService;

    @Getter
    private BottleServerMessageService bottleServerMessageService;

    @Getter
    private BottleServerUserSettingService bottleServerUserSettingService;

    @Getter
    private BottleFileStreamService bottleFileStreamService;

    @Getter
    private BottleServerChatService bottleServerChatService;

    @Getter
    private BottleServerStompService bottleServerStompService;

    @Getter
    private BottleServerProfileService bottleServerProfileService;

    @Override
    public void clearCached() {
        bottleServerAuthService.clearCached();
        bottleServerCategoryService.clearCached();
        bottleServerGeoService.clearCached();
        bottleServerRoomService.clearCached();
        bottleServerMessageService.clearCached();
        bottleServerUserSettingService.clearCached();
        bottleFileStreamService.clearCached();
        bottleServerChatService.clearCached();
        bottleServerStompService.clearCached();
        bottleServerProfileService.clearCached();
    }

    @Override
    public void initialize(Context context) {
        bottleServerAuthService = new BottleServerAuthImpl();
        bottleServerCategoryService = new BottleServerCategoryServiceImpl();
        bottleServerGeoService = new BottleServerGeoServiceImpl();
        bottleServerRoomService = new BottleServerRoomServiceImpl();
        bottleServerMessageService = new BottleServerMessageServiceImpl();
        bottleServerUserSettingService = new BottleServerUserSettingServiceImpl();
        bottleFileStreamService = new BottleFileStreamServiceImpl();
        bottleServerChatService = new BottleServerChatServiceImpl();
        bottleServerStompService = new BottleServerStompServiceImpl();
        bottleServerProfileService = new BottleServerProfileServiceImpl();
    }
}
