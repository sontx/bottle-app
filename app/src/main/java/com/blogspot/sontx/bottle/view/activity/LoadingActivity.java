package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.presenter.LoadingPresenterImpl;
import com.blogspot.sontx.bottle.presenter.LoginPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.LoadingPresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.view.interfaces.LoadingView;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;

public class LoadingActivity extends ActivityBase implements LoginView, LoadingView {
    static final String LOGIN_DATA = "login_data";

    private LoginPresenter loginPresenter;
    private LoadingPresenter loadingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullscreen();
        setContentView(R.layout.activity_loading);

        loginPresenter = new LoginPresenterImpl(this);

        loadingPresenter = new LoadingPresenterImpl(this);
        loadingPresenter.registerListener();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(LOGIN_DATA)) {
            LoginData loginData = (LoginData) intent.getSerializableExtra(LOGIN_DATA);
            loginPresenter.facebookLoginAsync(loginData);
        } else {
            loginPresenter.checkLoginWithChatServer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingPresenter.unregisterListener();
    }

    @UiThread
    @Override
    public void onLoginStateChanged(boolean logged) {
        if (logged) {
            loadingPresenter.loadIfNecessaryAsync();
        } else {
            loginPresenter.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @UiThread
    @Override
    public void navigateToErrorActivity(String message) {
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra(ErrorActivity.MESSAGE_KEY, message);
        startActivity(intent);
        finish();
    }

    @UiThread
    @Override
    public void onLoadSuccess() {
        startActivity(new Intent(LoadingActivity.this, HomeActivity.class));
        finish();
    }
}
