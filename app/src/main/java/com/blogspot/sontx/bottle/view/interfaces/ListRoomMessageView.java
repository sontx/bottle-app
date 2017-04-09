package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.RoomMessage;

import java.util.List;

public interface ListRoomMessageView extends ViewBase {
    void showRoomMessages(List<RoomMessage> result);
}
