package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.RoomMessage;

public interface RoomMessageChangeView extends ViewBase {
    void addRoomMessage(RoomMessage message);

    void updateRoomMessage(RoomMessage message);

    void removeRoomMessage(int messageId);
}
