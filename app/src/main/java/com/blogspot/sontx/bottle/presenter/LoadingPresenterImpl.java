package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.presenter.interfaces.LoadingPresenter;
import com.blogspot.sontx.bottle.view.interfaces.LoadingView;

public class LoadingPresenterImpl
        extends PresenterBase
        implements LoadingPresenter {

    private final LoadingView loadingView;

    public LoadingPresenterImpl(LoadingView loadingView) {
        this.loadingView = loadingView;
    }

    @Override
    public void loadLasSessionDataAsync() {
        loadingView.onLoadSuccess();
    }
}
