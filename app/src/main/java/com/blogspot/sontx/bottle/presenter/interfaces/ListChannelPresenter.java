package com.blogspot.sontx.bottle.presenter.interfaces;

public interface ListChannelPresenter {
    void updateChannelsIfNecessary();

    void registerEvents();

    void unregisterEvents();
}
