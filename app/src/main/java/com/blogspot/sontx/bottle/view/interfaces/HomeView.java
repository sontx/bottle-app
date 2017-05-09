package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;

public interface HomeView extends ViewBase {
    void startChatWithExistingChannel(Channel channel);

    void startChatWithExistingChannel(String channelId);

    void startChatWithAnotherGuy(String anotherGuyId);

    void updateUIAfterLogout();
}
