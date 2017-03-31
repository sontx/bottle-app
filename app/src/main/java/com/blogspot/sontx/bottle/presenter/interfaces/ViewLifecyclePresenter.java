package com.blogspot.sontx.bottle.presenter.interfaces;

public interface ViewLifecyclePresenter {
    void onCreate();

    void onStart();

    void onStop();

    void onResume();

    void onDestroy();
}
