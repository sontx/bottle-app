package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.presenter.LoadingPresenterImpl;
import com.blogspot.sontx.bottle.presenter.LoginPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.LoadingPresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.view.interfaces.LoadingView;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;

public class LoadingActivity extends ActivityBase implements LoginView, LoadingView {
    private LoginPresenter loginPresenter;
    private LoadingPresenter loadingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullscreen();
        setContentView(R.layout.activity_loading);

        loginPresenter = new LoginPresenterImpl(this);
        loadingPresenter = new LoadingPresenterImpl(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginPresenter.onResume();
    }

    @Override
    public void updateUI(String userId) {
        if (userId != null) {
            loadingPresenter.setCurrentUserId(userId);
            loadingPresenter.loadIfNecessaryAsync();
        } else {
            navigateToErrorActivity(null);
        }
    }

    @Override
    public void onLoadSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoadingActivity.this, ListCategoryActivity.class));
                finish();
            }
        });
    }

    @Override
    public void navigateToErrorActivity(String message) {
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra(ErrorActivity.MESSAGE_KEY, message);
        startActivity(intent);
        finish();
    }
}
