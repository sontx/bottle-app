package com.blogspot.sontx.bottle.view.interfaces;

import com.google.firebase.auth.FirebaseUser;

public interface LoginView extends ViewBase {
    void updateUI(FirebaseUser user);
}
