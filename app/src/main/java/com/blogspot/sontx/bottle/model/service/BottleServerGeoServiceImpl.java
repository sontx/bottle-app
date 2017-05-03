package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerGeoService;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;
import com.blogspot.sontx.bottle.model.service.rest.ApiMap;

import retrofit2.Call;
import retrofit2.Response;

class BottleServerGeoServiceImpl extends BottleServerServiceBase implements BottleServerGeoService {

    @Override
    public void postGeoMessageAsync(GeoMessage geoMessage, final Callback<GeoMessage> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiMap apiMap = ApiClient.getClient(bottleUser.getToken()).create(ApiMap.class);

        final Call<GeoMessage> call = apiMap.postMessage(geoMessage);

        call.enqueue(new retrofit2.Callback<GeoMessage>() {
            @Override
            public void onResponse(Call<GeoMessage> call, Response<GeoMessage> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "postGeoMessageAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<GeoMessage> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public void editGeoMessageAsync(GeoMessage geoMessage, final Callback<GeoMessage> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiMap apiMap = ApiClient.getClient(bottleUser.getToken()).create(ApiMap.class);

        final Call<GeoMessage> call = apiMap.editMessage(geoMessage.getId(), geoMessage);

        call.enqueue(new retrofit2.Callback<GeoMessage>() {
            @Override
            public void onResponse(Call<GeoMessage> call, Response<GeoMessage> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "editGeoMessageAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<GeoMessage> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

}
