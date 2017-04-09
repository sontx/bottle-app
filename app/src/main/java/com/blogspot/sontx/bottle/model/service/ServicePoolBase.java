package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerCategoryService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;

import lombok.Getter;

abstract class ServicePoolBase implements ServicePool {
    @Getter
    private BottleServerAuthService bottleServerAuthService;

    @Getter
    private BottleServerCategoryService bottleServerCategoryService;

    @Getter
    private BottleServerRoomService bottleServerRoomService;

    @Override
    public void initialize(Context context) {
        bottleServerAuthService = new BottleServerAuthImpl();
        bottleServerCategoryService = new BottleServerCategoryServiceImpl(bottleServerAuthService);
        bottleServerRoomService = new BottleServerRoomServiceImpl(bottleServerAuthService);
    }
}
