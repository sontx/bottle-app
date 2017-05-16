package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiProfile {
    @PUT("profile/{userId}")
    Call<PublicProfile> updateProfile(@Path("userId") String userId, @Body PublicProfile profile);
}
