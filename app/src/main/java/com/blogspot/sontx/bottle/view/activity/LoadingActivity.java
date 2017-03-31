package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.presenter.LoadingPresenterImpl;
import com.blogspot.sontx.bottle.presenter.LoginPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.LoadingPresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.view.interfaces.LoadingView;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;
import com.google.firebase.auth.FirebaseUser;

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
    public void updateUI(FirebaseUser user) {
        if (user != null) {
            TextView welcomeTextView = (TextView) findViewById(R.id.welcome_text);
            welcomeTextView.setText(user.getDisplayName());
            loadingPresenter.loadLasSessionDataAsync();
        } else {
            startActivity(new Intent(this, FbLoginActivity.class));
            finish();
        }
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
}
