package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.model.bean.UserSetting;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiUserSetting {
    @GET("settings/{userId}")
    Call<UserSetting> getUserSetting(@Path("userId") String userId);

    @PUT("settings/{userId}")
    Call<UserSetting> updateUserSetting(@Path("userId") String userId, @Body UserSetting userSetting);
}
