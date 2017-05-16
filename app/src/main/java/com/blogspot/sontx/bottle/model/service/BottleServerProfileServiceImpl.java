package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerProfileService;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;
import com.blogspot.sontx.bottle.model.service.rest.ApiProfile;

import retrofit2.Call;
import retrofit2.Response;

class BottleServerProfileServiceImpl extends BottleServerServiceBase implements BottleServerProfileService {

    @Override
    public void updateProfileAsync(PublicProfile publicProfile, final Callback<PublicProfile> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiProfile apiProfile = ApiClient.getClient(bottleUser.getToken()).create(ApiProfile.class);

        Call<PublicProfile> call = apiProfile.updateProfile(bottleUser.getUid(), publicProfile);

        call.enqueue(new retrofit2.Callback<PublicProfile>() {
            @Override
            public void onResponse(Call<PublicProfile> call, Response<PublicProfile> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "updateProfileAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<PublicProfile> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
