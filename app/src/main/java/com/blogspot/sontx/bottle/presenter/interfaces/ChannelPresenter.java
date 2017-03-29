package com.blogspot.sontx.bottle.presenter.interfaces;

public interface ChannelPresenter {
    void getCurrentChannelsAsync();

    void createChannelAsync(String anotherMemberId);
}
