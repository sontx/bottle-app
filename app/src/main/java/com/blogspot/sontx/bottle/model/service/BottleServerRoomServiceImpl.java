package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;
import com.blogspot.sontx.bottle.model.service.rest.ApiRoom;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

class BottleServerRoomServiceImpl extends BottleServerServiceBase implements BottleServerRoomService {
    private final BottleServerAuthService bottleServerAuthService;

    BottleServerRoomServiceImpl(BottleServerAuthService bottleServerAuthService) {
        this.bottleServerAuthService = bottleServerAuthService;
    }

    @Override
    public void getRoomsAsync(int categoryId, final Callback<List<Room>> callback) {
        BottleUser bottleUser = bottleServerAuthService.getCurrentBottleUser();
        if (bottleUser == null || bottleUser.getToken() == null) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        ApiRoom apiRoom = ApiClient.getClient(bottleUser.getToken()).create(ApiRoom.class);

        Call<List<Room>> call = apiRoom.getRooms(categoryId, 0, 100);// assume that we want to get all rooms

        call.enqueue(new retrofit2.Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "getRoomsAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
