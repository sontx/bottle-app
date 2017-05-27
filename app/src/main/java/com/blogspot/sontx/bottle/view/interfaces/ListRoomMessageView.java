package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.RoomMessage;

import java.util.List;

public interface ListRoomMessageView extends ViewBase {
    void showRoomMessages(List<RoomMessage> result);

    void addRoomMessage(RoomMessage roomMessage);

    void updateRoomMessage(RoomMessage roomMessage, RoomMessage originRoomMessage);

    void showListRoomsByCategoryId(int categoryId, int currentRoomId);

    void showListRoomsByRoomId(int roomId);

    void clearRoomMessages();
}
