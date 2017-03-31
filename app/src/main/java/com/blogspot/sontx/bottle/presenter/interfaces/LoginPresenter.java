package com.blogspot.sontx.bottle.presenter.interfaces;

public interface LoginPresenter extends ViewLifecyclePresenter {
    void loginAsync(String token);

    void logout();
}
