package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.service.Callback;

import java.util.List;

public interface BottleServerRoomService extends ServiceBase {
    void getRoomsAsync(int categoryId, Callback<List<Room>> callback);

    void postRoomMessageAsync(int roomId, RoomMessage roomMessage, Callback<RoomMessage> callback);
}
