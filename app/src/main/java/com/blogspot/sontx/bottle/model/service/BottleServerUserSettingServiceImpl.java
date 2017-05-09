package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerUserSettingService;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;
import com.blogspot.sontx.bottle.model.service.rest.ApiUserSetting;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Response;

class BottleServerUserSettingServiceImpl extends BottleServerServiceBase implements BottleServerUserSettingService {
    @Getter
    private UserSetting currentUserSetting;

    @Override
    public void clearCached() {
        super.clearCached();
        currentUserSetting = null;
    }

    @Override
    public void getUserSettingAsync(final Callback<UserSetting> callback) {
        if (currentUserSetting != null) {
            callback.onSuccess(currentUserSetting);
            return;
        }

        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiUserSetting apiUserSetting = ApiClient.getClient(bottleUser.getToken()).create(ApiUserSetting.class);

        Call<UserSetting> call = apiUserSetting.getUserSetting(bottleUser.getUid());

        call.enqueue(new retrofit2.Callback<UserSetting>() {
            @Override
            public void onResponse(Call<UserSetting> call, Response<UserSetting> response) {
                if (response.code() == 200) {
                    currentUserSetting = response.body();
                    callback.onSuccess(currentUserSetting);
                } else {
                    Log.e(TAG, "getRoomsAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<UserSetting> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public void updateUserSettingAsync(final UserSetting userSetting, final Callback<UserSetting> callback) {
        this.currentUserSetting = userSetting;

        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiUserSetting apiUserSetting = ApiClient.getClient(bottleUser.getToken()).create(ApiUserSetting.class);

        Call<UserSetting> call = apiUserSetting.updateUserSetting(bottleUser.getUid(), userSetting);

        call.enqueue(new retrofit2.Callback<UserSetting>() {
            @Override
            public void onResponse(Call<UserSetting> call, Response<UserSetting> response) {
                if (response.code() == 200) {
                    currentUserSetting = response.body();
                    callback.onSuccess(userSetting);
                } else {
                    Log.e(TAG, "updateUserSettingAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<UserSetting> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
