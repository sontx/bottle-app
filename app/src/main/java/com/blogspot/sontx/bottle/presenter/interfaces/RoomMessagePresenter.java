package com.blogspot.sontx.bottle.presenter.interfaces;

public interface RoomMessagePresenter {
    void setCurrentRoomId(int roomId);

    void getMoreRoomMessagesAsync();
}