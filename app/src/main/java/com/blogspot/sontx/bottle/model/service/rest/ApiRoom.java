package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.model.bean.Room;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRoom {
    @GET("categories/{categoryId}/rooms")
    Call<List<Room>> getRooms(@Path("categoryId") int categoryId, @Query("page") int page, @Query("pageSize") int pageSize);
}