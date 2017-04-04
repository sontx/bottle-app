package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;

public interface LoginService extends ServiceBase {
    void getCurrentUserTokenAsync(SimpleCallback<String> onCompleted);

    void facebookLoginAsync(LoginData loginData, SimpleCallback<String> onCompleted);

    void signOut(SimpleCallback<String> onCompleted);

    String getCurrentUserId();
}
