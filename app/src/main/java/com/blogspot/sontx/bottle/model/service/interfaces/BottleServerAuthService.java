package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.service.Callback;

public interface BottleServerAuthService extends ServiceBase {
    BottleUser getCurrentBottleUser();

    void loginWithTokenAsync(String verifyToken, Callback<BottleUser> resultCallback);
}
