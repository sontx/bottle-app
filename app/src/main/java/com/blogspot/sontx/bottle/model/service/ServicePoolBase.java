package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.BottleCategoryService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;

import lombok.Getter;

abstract class ServicePoolBase implements ServicePool {
    @Getter
    private BottleServerAuthService bottleServerAuthService;

    @Getter
    private BottleCategoryService bottleCategoryService;

    @Override
    public void initialize(Context context) {
        bottleServerAuthService = new BottleServerAuthImpl();
        bottleCategoryService = new BottleCategoryServiceImpl(bottleServerAuthService);
    }
}