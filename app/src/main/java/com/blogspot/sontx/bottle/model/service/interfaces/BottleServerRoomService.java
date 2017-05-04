package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.service.Callback;

public interface BottleServerRoomService extends ServiceBase {

    void postRoomMessageAsync(int roomId, RoomMessage roomMessage, Callback<RoomMessage> callback);

    void getRoomAsync(int roomId, Callback<Room> callback);

    void editRoomMessageAsync(RoomMessage roomMessage, Callback<RoomMessage> callback);
}
