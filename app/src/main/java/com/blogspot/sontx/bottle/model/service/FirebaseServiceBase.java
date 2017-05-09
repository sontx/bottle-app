package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.ServiceBase;
import com.firebase.client.Firebase;

abstract class FirebaseServiceBase implements ServiceBase {
    protected final static String TAG = "firebase";

    FirebaseServiceBase(Context context) {
        if (context != null)
            Firebase.setAndroidContext(context);
    }

    public void setContext(Context context) {
        Firebase.setAndroidContext(context);
    }

    @Override
    public void clearCached() {
    }
}
