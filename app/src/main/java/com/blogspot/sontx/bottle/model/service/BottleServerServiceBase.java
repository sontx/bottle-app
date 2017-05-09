package com.blogspot.sontx.bottle.model.service;

import com.blogspot.sontx.bottle.model.service.interfaces.ServiceBase;

abstract class BottleServerServiceBase implements ServiceBase {
    protected static final String TAG = "bottle-server";

    @Override
    public void clearCached() {
    }
}
