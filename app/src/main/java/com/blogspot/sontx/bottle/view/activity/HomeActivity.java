package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.presenter.AccountManagerPresenterImpl;
import com.blogspot.sontx.bottle.presenter.LoginPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.AccountManagerPresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.view.interfaces.AccountManagerView;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends ActivityBase implements AccountManagerView {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };
    private AccountManagerPresenter accountManagerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullscreen();
        setContentView(R.layout.activity_home);

//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        accountManagerPresenter = new AccountManagerPresenterImpl(this);
        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountManagerPresenter.logout();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        accountManagerPresenter.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
        accountManagerPresenter.unregister();
    }

    @Override
    public void updateUI(FirebaseUser user) {
        if (user == null) {
            startActivity(new Intent(this, FbLoginActivity.class));
            finish();
        }
    }
}
