package com.blogspot.sontx.bottle.model.service;

import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.LoginData;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;
import com.blogspot.sontx.bottle.model.service.rest.ApiLogin;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Response;

class BottleServerAuthImpl extends BottleServerServiceBase implements BottleServerAuthService {
    @Getter
    private BottleUser currentBottleUser;

    @Override
    public void loginWithTokenAsync(String verifyToken, final Callback<BottleUser> resultCallback) {
        ApiLogin apiLogin = ApiClient.getClient().create(ApiLogin.class);

        LoginData loginData = new LoginData();
        loginData.setToken(verifyToken);

        Call<BottleUser> call = apiLogin.login(loginData);
        call.enqueue(new retrofit2.Callback<BottleUser>() {
            @Override
            public void onResponse(Call<BottleUser> call, Response<BottleUser> response) {
                currentBottleUser = response.body();
                resultCallback.onSuccess(currentBottleUser);
            }

            @Override
            public void onFailure(Call<BottleUser> call, Throwable t) {
                resultCallback.onError(t);
            }
        });
    }
}
