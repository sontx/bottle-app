package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.DeleteResult;
import com.blogspot.sontx.bottle.model.bean.chat.CreateChannelRequest;
import com.blogspot.sontx.bottle.model.bean.chat.CreateChannelResult;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerChatService;
import com.blogspot.sontx.bottle.model.service.rest.ApiChat;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;

import retrofit2.Call;
import retrofit2.Response;

class BottleServerChatServiceImpl extends BottleServerServiceBase implements BottleServerChatService {

    @Override
    public void createChannelAsync(String anotherGuyId, final Callback<CreateChannelResult> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiChat apiChat = ApiClient.getClient(bottleUser.getToken()).create(ApiChat.class);

        CreateChannelRequest createChannelRequest = new CreateChannelRequest();
        createChannelRequest.setBuddyId(anotherGuyId);

        Call<CreateChannelResult> call = apiChat.createChannel(createChannelRequest);

        call.enqueue(new retrofit2.Callback<CreateChannelResult>() {
            @Override
            public void onResponse(Call<CreateChannelResult> call, Response<CreateChannelResult> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "createChannelAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<CreateChannelResult> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public void deleteChannelAsync(String channelId, final Callback<Void> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiChat apiChat = ApiClient.getClient(bottleUser.getToken()).create(ApiChat.class);

        Call<DeleteResult> call = apiChat.deleteChannel(channelId);

        call.enqueue(new retrofit2.Callback<DeleteResult>() {
            @Override
            public void onResponse(Call<DeleteResult> call, Response<DeleteResult> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "deleted " + response.body());
                    callback.onSuccess(null);
                } else {
                    Log.e(TAG, "deleteChannelAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(response.code() + ""));
                }
            }

            @Override
            public void onFailure(Call<DeleteResult> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
