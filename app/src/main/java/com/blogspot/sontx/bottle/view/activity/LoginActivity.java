package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.system.provider.Auth2Provider;
import com.blogspot.sontx.bottle.system.provider.FacebookAuth2Provider;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends ActivityBase {
    @BindView(R.id.fb_login_button)
    LoginButton facebookButton;
    private Auth2Provider facebookAuth2Provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullscreen();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }

        facebookAuth2Provider = new FacebookAuth2Provider(facebookButton);
        facebookAuth2Provider.setOnAuthCompleted(new SimpleCallback<LoginData>() {
            @Override
            public void onCallback(LoginData value) {
                Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
                intent.putExtra(LoadingActivity.LOGIN_DATA, value);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookAuth2Provider.onActivityResult(requestCode, resultCode, data);
    }
}
