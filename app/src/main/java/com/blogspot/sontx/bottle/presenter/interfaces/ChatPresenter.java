package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;

public interface ChatPresenter extends ViewLifecyclePresenter {
    void sendAsync(String text);

    void setChannel(Channel channel);

    PublicProfile getCurrentPublicProfile();
}
