package com.blogspot.sontx.bottle.presenter.interfaces;

public interface ChatChannelPresenter {
    void setup(String userId);

    void fetchAvailableChatChannelsAsync();
}
