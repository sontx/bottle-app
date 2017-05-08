package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.model.bean.GeoMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiMap {
    @GET("geo/messages")
    Call<List<GeoMessage>> getMessagesAroundLocation(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("latitudeRadius") double latitudeRadius,
            @Query("longitudeRadius") double longitudeRadius);

    @GET("geo/messages/{messageId}")
    Call<GeoMessage> getMessage(@Path("messageId") int messageId);

    @POST("geo/messages")
    Call<GeoMessage> postMessage(@Body GeoMessage geoMessage);

    @PUT("geo/messages/{messageId}")
    Call<GeoMessage> editMessage(@Path("messageId") int messageId, @Body GeoMessage geoMessage);

    @DELETE("geo/messages/{messageId}")
    Call<GeoMessage> deleteMessage(@Path("messageId") int messageId);
}
