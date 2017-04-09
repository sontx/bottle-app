package com.blogspot.sontx.bottle.presenter.interfaces;

public interface LoadingPresenter {
    void registerListener();

    void unregisterListener();

    void loadIfNecessaryAsync();
}
