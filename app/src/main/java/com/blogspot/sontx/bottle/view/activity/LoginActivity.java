package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.presenter.LoginPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.system.provider.Auth2Provider;
import com.blogspot.sontx.bottle.system.provider.FacebookAuth2Provider;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends ActivityBase implements LoginView {
    @BindView(R.id.fb_login_button)
    LoginButton facebookButton;
    private LoginPresenter loginPresenter;
    private Auth2Provider facebookAuth2Provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullscreen();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginPresenter = new LoginPresenterImpl(this);

        facebookAuth2Provider = new FacebookAuth2Provider(facebookButton);
        facebookAuth2Provider.setOnAuthCompleted(new SimpleCallback<LoginData>() {
            @Override
            public void onCallback(LoginData value) {
                loginPresenter.facebookLoginAsync(value);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookAuth2Provider.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateUI(final String userId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (userId != null) {
                    Log.d(TAG, "login with: " + userId);
                    startActivity(new Intent(LoginActivity.this, LoadingActivity.class));
                    finish();
                }
            }
        });
    }
}
