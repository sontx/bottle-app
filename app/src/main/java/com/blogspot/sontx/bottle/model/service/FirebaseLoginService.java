package com.blogspot.sontx.bottle.model.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.service.interfaces.LoginService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class FirebaseLoginService extends FirebaseServiceBase implements LoginService {
    private final FirebaseAuth firebaseAuth;

    FirebaseLoginService(Context context) {
        super(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void facebookLoginAsync(LoginData loginData, final SimpleCallback<String> onCompleted) {
        AuthCredential credential = FacebookAuthProvider.getCredential(loginData.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                    onCompleted.onCallback(task.getResult().getUser().getUid());
                else
                    onCompleted.onCallback(null);
            }
        });
    }

    @Override
    public void signOut(SimpleCallback<String> onCompleted) {
        firebaseAuth.addAuthStateListener(new AuthStateChangedHandler(onCompleted));
        firebaseAuth.signOut();
    }

    @Override
    public String getCurrentUserId() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    private static class AuthStateChangedHandler implements FirebaseAuth.AuthStateListener {
        private final SimpleCallback<String> onCompleted;

        AuthStateChangedHandler(SimpleCallback<String> onCompleted) {
            this.onCompleted = onCompleted;
        }

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            onCompleted.onCallback(currentUser != null ? currentUser.getUid() : null);
            firebaseAuth.removeAuthStateListener(this);
        }
    }
}
