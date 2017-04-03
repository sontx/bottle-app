package com.blogspot.sontx.bottle.view.interfaces;

public interface LoadingView extends ViewBase {
    void onLoadSuccess();

    void navigateToErrorActivity(String message);
}
