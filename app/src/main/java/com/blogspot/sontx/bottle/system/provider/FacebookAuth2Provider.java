package com.blogspot.sontx.bottle.system.provider;

import android.content.Intent;
import android.util.Log;

import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import lombok.Setter;

public final class FacebookAuth2Provider extends Auth2ProviderBase implements FacebookCallback<LoginResult> {
    private static final String[] FACEBOOK_PERMISSIONS = {"email", "public_profile", "user_location"};
    private CallbackManager callbackManager;
    @Setter
    private SimpleCallback<LoginData> onAuthCompleted;

    public FacebookAuth2Provider(LoginButton loginButton) {
        setupFacebookLogin(loginButton);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void logout() {
        LoginManager.getInstance().logOut();
    }

    private void setupFacebookLogin(LoginButton loginButton) {
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(FACEBOOK_PERMISSIONS);
        loginButton.registerCallback(callbackManager, this);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        if (onAuthCompleted != null) {
            LoginData loginData = new LoginData();
            loginData.setState(LoginData.STATE_SUCCESS);
            loginData.setToken(loginResult.getAccessToken().getToken());

            onAuthCompleted.onCallback(loginData);
        }
        Log.d(TAG, "Login with facebook is successful: " + loginResult.getAccessToken().getUserId());
        currentProvider = this;
    }

    @Override
    public void onCancel() {
        if (onAuthCompleted != null) {
            LoginData loginData = new LoginData();
            loginData.setState(LoginData.STATE_CANCEL);
            onAuthCompleted.onCallback(loginData);
        }
        Log.d(TAG, "Login with facebook is canceled.");
    }

    @Override
    public void onError(FacebookException error) {
        if (onAuthCompleted != null) {
            LoginData loginData = new LoginData();
            loginData.setState(LoginData.STATE_ERROR);
            onAuthCompleted.onCallback(loginData);
        }
        Log.d(TAG, "Login with facebook is error: " + error.getMessage());
    }
}
