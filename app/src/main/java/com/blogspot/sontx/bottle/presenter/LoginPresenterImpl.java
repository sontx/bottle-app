package com.blogspot.sontx.bottle.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.blogspot.sontx.bottle.presenter.interfaces.LoginPresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.PublicProfilePresenter;
import com.blogspot.sontx.bottle.view.interfaces.LoginView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPresenterImpl extends PresenterBase implements LoginPresenter, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {
    private final LoginView loginView;
    private final FirebaseAuth firebaseAuth;
    private final PublicProfilePresenter publicProfilePresenter;

    public LoginPresenterImpl(@lombok.NonNull LoginView loginView) {
        this.loginView = loginView;
        this.firebaseAuth = FirebaseAuth.getInstance();
        publicProfilePresenter = new PublicProfilePresenterImpl();
    }

    @Override
    public void loginAsync(String token) {
        loginView.showProcess();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this);
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
    public void checkLoginState() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null)
            publicProfilePresenter.updatePublicProfileIfEmptyAsync();
        loginView.updateUI(user);
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
        loginView.updateUI(null);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        loginView.updateUI(user);
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
        if (!task.isSuccessful()) {
            Log.w(TAG, "signInWithCredential", task.getException());
            loginView.showErrorMessage("Authentication failed.");
        } else {
            publicProfilePresenter.updatePublicProfileIfEmptyAsync();
        }
        loginView.hideProcess();
    }
}
