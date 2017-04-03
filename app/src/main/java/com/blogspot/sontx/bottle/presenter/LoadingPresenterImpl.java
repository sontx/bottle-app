package com.blogspot.sontx.bottle.presenter;

import android.os.AsyncTask;

import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.presenter.interfaces.LoadingPresenter;
import com.blogspot.sontx.bottle.view.interfaces.LoadingView;
import com.facebook.FacebookSdk;

import lombok.Setter;

public class LoadingPresenterImpl
        extends PresenterBase
        implements LoadingPresenter {

    private final LoadingView loadingView;
    @Setter
    private String currentUserId;
    private boolean needLoad = true;

    public LoadingPresenterImpl(LoadingView loadingView) {
        this.loadingView = loadingView;
    }

    @Override
    public void loadIfNecessaryAsync() {
        if (!needLoad)
            return;
        needLoad = false;

        FacebookSdk.sdkInitialize(loadingView.getContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                new AsyncTask<Object, Object, Object>() {

                    @Override
                    protected Object doInBackground(Object[] params) {
                        ChannelService channelService = FirebaseServicePool.getInstance().getChannelService();
                        channelService.cacheChannelsAsync(currentUserId, new Callback<Void>() {

                            @Override
                            public void onSuccess(Void result) {
                                loadingView.onLoadSuccess();
                            }

                            @Override
                            public void onError(Throwable what) {
                                loadingView.showErrorMessage(what.toString());
                                loadingView.navigateToErrorActivity(what.getMessage());
                            }
                        });
                        return null;
                    }
                }.execute();

            }
        });
    }
}
