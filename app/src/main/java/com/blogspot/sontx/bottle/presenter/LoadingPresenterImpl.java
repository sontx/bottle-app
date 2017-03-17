package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.presenter.interfaces.LoadingPresenter;
import com.blogspot.sontx.bottle.view.activity.LoadingActivity;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                loadingView.onLoadSuccess();
            }
        }).start();
    }
}
