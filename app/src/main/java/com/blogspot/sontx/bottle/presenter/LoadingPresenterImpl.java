package com.blogspot.sontx.bottle.presenter;

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

    public LoadingPresenterImpl(LoadingView loadingView) {
        this.loadingView = loadingView;
    }

    @Override
    public void loadAsync() {
        FacebookSdk.sdkInitialize(loadingView.getContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                FirebaseServicePool.getInstance().initialize(loadingView.getContext());

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
            }
        });
    }
}
