package com.blogspot.sontx.bottle.presenter;

import android.support.annotation.NonNull;

import com.blogspot.sontx.bottle.presenter.interfaces.AccountManagerPresenter;
import com.blogspot.sontx.bottle.view.interfaces.AccountManagerView;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class AccountManagerPresenterImpl
        extends PresenterBase
        implements AccountManagerPresenter, FirebaseAuth.AuthStateListener {

    private final AccountManagerView accountManagerView;
    private final FirebaseAuth firebaseAuth;

    public AccountManagerPresenterImpl(AccountManagerView accountManagerView) {
        this.accountManagerView = accountManagerView;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    @Override
    public void register() {
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    public void unregister() {
        firebaseAuth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        accountManagerView.updateUI(firebaseAuth.getCurrentUser());
    }
}
