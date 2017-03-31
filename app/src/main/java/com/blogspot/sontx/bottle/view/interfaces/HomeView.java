package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;

public interface HomeView extends ViewBase {
    void startChat(Channel channel, String currentUserId);
}
