package com.blogspot.sontx.bottle.presenter.interfaces;

public interface RoomMessagePresenter {
    void setCurrentRoomId(int roomId);

    void getMoreRoomMessagesAsync();

    void postRoomMessageAsync(String text, String mediaPath);

    void jumpToListRooms();

    void selectRoom(int roomId);
}
