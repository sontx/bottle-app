package com.blogspot.sontx.bottle.model.service;

import android.content.Context;

import com.blogspot.sontx.bottle.model.service.interfaces.ServiceBase;
import com.firebase.client.Firebase;

abstract class FirebaseServiceBase implements ServiceBase {
    FirebaseServiceBase(Context context) {
        Firebase.setAndroidContext(context);
    }
}
