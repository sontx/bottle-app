package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;

public interface HomePresenter {
    void startChat(Channel channel);

    void directMessage(MessageBase message);

    void updateMessageAsync(MessageBase messageBase);
}
