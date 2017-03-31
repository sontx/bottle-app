package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.presenter.interfaces.ViewLifecyclePresenter;

abstract class PresenterBase implements ViewLifecyclePresenter {
    protected static final String TAG = "PRESENTER";

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onDestroy() {
    }
}
