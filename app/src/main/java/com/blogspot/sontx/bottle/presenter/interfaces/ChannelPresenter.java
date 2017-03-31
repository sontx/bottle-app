package com.blogspot.sontx.bottle.presenter.interfaces;

public interface ChannelPresenter extends ViewLifecyclePresenter {
    void updateChannelsIfNecessary();

    void createChannelAsync(String anotherMemberId);

    String getCurrentUserId();
}
