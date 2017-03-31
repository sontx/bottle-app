package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;

public interface HomePresenter extends ViewLifecyclePresenter {
    void startChat(Channel channel);

    void switchCurrentUserId(String currentUserId);
}
