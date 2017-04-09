package com.blogspot.sontx.bottle.presenter.interfaces;

public interface ListChannelPresenter {
    void updateChannelsIfNecessary();

    void createChannelAsync(String anotherMemberId);
}
