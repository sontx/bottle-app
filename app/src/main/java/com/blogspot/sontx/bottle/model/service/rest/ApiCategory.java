package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.bean.RoomList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiCategory {
    @GET("categories/{categoryId}/rooms")
    Call<RoomList> getRooms(@Path("categoryId") int categoryId, @Query("page") int page, @Query("pageSize") int pageSize);

    @GET("categories")
    Call<List<Category>> getCategories(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("categories/{categoryId}")
    Call<Category> getCategory(@Path("categoryId") int categoryId);
}
