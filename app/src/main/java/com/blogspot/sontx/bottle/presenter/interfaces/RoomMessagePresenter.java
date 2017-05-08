package com.blogspot.sontx.bottle.presenter.interfaces;

import com.blogspot.sontx.bottle.model.bean.MessageBase;

public interface RoomMessagePresenter {
    void setCurrentRoomId(int roomId);

    void getMoreRoomMessagesAsync();

    void postRoomMessageAsync(String text, String mediaPath, String type);

    void jumpToListRooms();

    void selectRoom(int roomId);

    void deleteMessageAsync(MessageBase messageBase);
}
