package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.service.Callback;

import java.util.List;

public interface BottleServerMessageService extends ServiceBase {
    void getRoomMessages(int roomId, int page, int pageSize, Callback<List<RoomMessage>> callback);

    void cacheMessagesAsync(Callback<Void> callback);

    void getMapMessagesAroundLocationAsync(double latitude,
                                           double longitude,
                                           double latitudeRadius,
                                           double longitudeRadius,
                                           Callback<List<GeoMessage>> callback);
}
