package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.LoginData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiLogin {
    @POST("login")
    Call<BottleUser> login(@Body LoginData loginData);
}
