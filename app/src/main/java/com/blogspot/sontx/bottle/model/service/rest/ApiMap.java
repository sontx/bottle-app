package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.model.bean.GeoMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiMap {
    @GET("geo/messages")
    Call<List<GeoMessage>> getMessagesAroundLocation(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("latitudeRadius") double latitudeRadius,
            @Query("longitudeRadius") double longitudeRadius);
}
