package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;
import com.blogspot.sontx.bottle.model.service.rest.ApiMap;
import com.blogspot.sontx.bottle.model.service.rest.ApiRoom;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

class BottleServerMessageServiceImpl extends BottleServerServiceBase implements BottleServerMessageService {
    private List<RoomMessage> cachedRoomMessages = null;

    @Override
    public void getRoomMessages(int roomId, int page, int pageSize, final Callback<List<RoomMessage>> callback) {
        if (cachedRoomMessages != null) {
            if (page == 0) {
                callback.onSuccess(cachedRoomMessages);
                cachedRoomMessages = null;
                return;
            } else {
                cachedRoomMessages = null;
            }
        }

        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiRoom apiRoom = ApiClient.getClient(bottleUser.getToken()).create(ApiRoom.class);

        Call<List<RoomMessage>> call = apiRoom.getRoomMessages(roomId, 0, 100);// assume that we want to get all rooms

        call.enqueue(new retrofit2.Callback<List<RoomMessage>>() {
            @Override
            public void onResponse(Call<List<RoomMessage>> call, Response<List<RoomMessage>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "getRoomsAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<List<RoomMessage>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public void cacheMessagesAsync(final Callback<Void> callback) {
        UserSetting currentUserSetting = App.getInstance().getBottleContext().getCurrentUserSetting();

        getRoomMessages(currentUserSetting.getCurrentRoomId(), 0, 20, new Callback<List<RoomMessage>>() {
            @Override
            public void onSuccess(List<RoomMessage> result) {
                cachedRoomMessages = result;
                callback.onSuccess(null);
            }

            @Override
            public void onError(Throwable what) {
                callback.onError(what);
            }
        });
    }

    @Override
    public void getMapMessagesAroundLocationAsync(double latitude,
                                                  double longitude,
                                                  double latitudeRadius,
                                                  double longitudeRadius,
                                                  final Callback<List<GeoMessage>> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiMap apiMap = ApiClient.getClient(bottleUser.getToken()).create(ApiMap.class);

        Call<List<GeoMessage>> call = apiMap.getMessagesAroundLocation(latitude, longitude, latitudeRadius, longitudeRadius);

        call.enqueue(new retrofit2.Callback<List<GeoMessage>>() {
            @Override
            public void onResponse(Call<List<GeoMessage>> call, Response<List<GeoMessage>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "getMapMessagesAroundLocationAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<List<GeoMessage>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
