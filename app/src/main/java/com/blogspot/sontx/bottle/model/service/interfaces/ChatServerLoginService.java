package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;

public interface ChatServerLoginService extends ServiceBase {
    void getCurrentUserTokenAsync(Callback<String> onCompleted);

    void facebookLoginAsync(LoginData loginData, SimpleCallback<String> onCompleted);

    void signOut(SimpleCallback<String> onCompleted);

    boolean isLoggedIn();
}
