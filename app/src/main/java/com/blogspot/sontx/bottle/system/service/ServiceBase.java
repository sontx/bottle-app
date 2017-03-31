package com.blogspot.sontx.bottle.system.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

abstract class ServiceBase extends Service {
    protected static final String TAG = "service";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
