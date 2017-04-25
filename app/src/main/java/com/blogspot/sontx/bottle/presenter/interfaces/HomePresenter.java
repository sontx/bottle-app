package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;

public interface HomePresenter {
    void startChat(Channel channel);

    void directMessage(RoomMessage roomMessage);
}
