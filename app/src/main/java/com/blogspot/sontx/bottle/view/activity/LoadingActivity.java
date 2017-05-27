package com.blogspot.sontx.bottle.view.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
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

        FirebaseServicePool.getInstance().initialize(this);

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
    protected void onDestroy() {
        super.onDestroy();
        loadingPresenter.unregisterListener();
    }

    @Override
    public void onLoginStateChanged(final boolean logged) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (logged) {
                    loadingPresenter.loadIfNecessaryAsync();
                } else {
                    loginPresenter.logout();
                    startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void navigateToErrorActivity(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, ErrorActivity.class);
                intent.putExtra(ErrorActivity.MESSAGE_KEY, message);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onLoadSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoadingActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
