package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.presenter.interfaces.PrivateProfilePresenter;
import com.blogspot.sontx.bottle.view.interfaces.PrivateProfileView;
import com.google.firebase.auth.FirebaseAuth;

public class PrivateProfilePresenterImpl extends PresenterBase implements PrivateProfilePresenter {
    private final PrivateProfileView privateProfileView;
    private final FirebaseAuth firebaseAuth;

    public PrivateProfilePresenterImpl(PrivateProfileView privateProfileView) {
        this.privateProfileView = privateProfileView;
        firebaseAuth = FirebaseAuth.getInstance();
    }

}
