package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.BottleFileStreamService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerCategoryService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerUserSettingService;

import lombok.Getter;

abstract class ServicePoolBase implements ServicePool {
    @Getter
    private BottleServerAuthService bottleServerAuthService;

    @Getter
    private BottleServerCategoryService bottleServerCategoryService;

    @Getter
    private BottleServerRoomService bottleServerRoomService;

    @Getter
    private BottleServerMessageService bottleServerMessageService;

    @Getter
    private BottleServerUserSettingService bottleServerUserSettingService;

    @Getter
    private BottleFileStreamService bottleFileStreamService;

    @Override
    public void initialize(Context context) {
        bottleServerAuthService = new BottleServerAuthImpl();
        bottleServerCategoryService = new BottleServerCategoryServiceImpl();
        bottleServerRoomService = new BottleServerRoomServiceImpl();
        bottleServerMessageService = new BottleServerMessageServiceImpl();
        bottleServerUserSettingService = new BottleServerUserSettingServiceImpl();
        bottleFileStreamService = new BottleFileStreamServiceImpl();
    }
}
