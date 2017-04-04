package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.presenter.LoginPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ErrorActivity extends ActivityBase implements LoginView {
    static final String MESSAGE_KEY = "message";

    @BindView(R.id.message_text)
    TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            String message = intent.getStringExtra(MESSAGE_KEY);
            if (message != null && message.length() > 0)
                messageView.setText(message);
        }
    }

    @OnClick(R.id.try_again_button)
    void tryAgainClick() {
        startActivity(new Intent(this, LoadingActivity.class));
        finish();
    }

    @OnClick(R.id.login_with_another_button)
    void tryAnotherAccountClick() {
        LoginPresenter loginPresenter = new LoginPresenterImpl(this);
        loginPresenter.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void updateUI(String userIdo) {

    }
}
