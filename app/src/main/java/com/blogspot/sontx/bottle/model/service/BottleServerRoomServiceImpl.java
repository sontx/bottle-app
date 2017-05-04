package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;
import com.blogspot.sontx.bottle.model.service.rest.ApiRoom;

import retrofit2.Call;
import retrofit2.Response;

class BottleServerRoomServiceImpl extends BottleServerServiceBase implements BottleServerRoomService {

    @Override
    public void postRoomMessageAsync(int roomId, RoomMessage roomMessage, final Callback<RoomMessage> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiRoom apiRoom = ApiClient.getClient(bottleUser.getToken()).create(ApiRoom.class);

        Call<RoomMessage> call = apiRoom.postRoomMessage(roomId, roomMessage);

        call.enqueue(new retrofit2.Callback<RoomMessage>() {
            @Override
            public void onResponse(Call<RoomMessage> call, Response<RoomMessage> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "postRoomMessageAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<RoomMessage> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public void getRoomAsync(int roomId, final Callback<Room> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiRoom apiRoom = ApiClient.getClient(bottleUser.getToken()).create(ApiRoom.class);

        final Call<Room> call = apiRoom.getRoom(roomId);

        call.enqueue(new retrofit2.Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "getRoomAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                callback.onError(t);
            }
        });
    }


    @Override
    public void editRoomMessageAsync(RoomMessage roomMessage, final Callback<RoomMessage> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiRoom apiRoom = ApiClient.getClient(bottleUser.getToken()).create(ApiRoom.class);

        Call<RoomMessage> call = apiRoom.editMessage(roomMessage.getId(), roomMessage);

        call.enqueue(new retrofit2.Callback<RoomMessage>() {
            @Override
            public void onResponse(Call<RoomMessage> call, Response<RoomMessage> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "editRoomMessageAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<RoomMessage> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
