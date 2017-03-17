package com.blogspot.sontx.bottle.presenter.interfaces;

public interface LoginPresenter {
    void loginAsync(String token);

    void register();

    void unregister();

    void checkLoginState();
}
