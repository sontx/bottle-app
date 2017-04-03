package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.presenter.LoginPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseUser;

public class FbLoginActivity extends ActivityBase implements FacebookCallback<LoginResult>, LoginView {
    private static final String[] FACEBOOK_PERMISSIONS = {"email", "public_profile", "user_location"};
    private CallbackManager callbackManager;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullscreen();
        setContentView(R.layout.activity_fb_login);

        loginPresenter = new LoginPresenterImpl(this);

        setupFacebookLogin();
    }

    @Override
    public void updateUI(final FirebaseUser user) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    Log.d(TAG, "login with: " + user.getDisplayName());
                    startActivity(new Intent(FbLoginActivity.this, LoadingActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginPresenter.onStop();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d(TAG, "facebook:onSuccess:" + loginResult);
        handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "facebook:onCancel");
        updateUI(null);
    }

    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "facebook:onError", error);
        updateUI(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(FACEBOOK_PERMISSIONS);
        loginButton.registerCallback(callbackManager, this);
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);
        loginPresenter.loginAsync(accessToken.getToken());
    }
}
