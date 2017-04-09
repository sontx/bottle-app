package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.LoginData;

public interface LoginPresenter {
    void facebookLoginAsync(LoginData loginData);

    void logout();

    void checkLoginWithChatServer();
}
