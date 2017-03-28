package com.blogspot.sontx.bottle.view.interfaces;

import com.google.firebase.auth.FirebaseUser;

public interface PrivateProfileView extends ViewBase {
    void updateUI(FirebaseUser currentUser);
}
