package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.model.bean.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiCategory {
    @GET("categories")
    Call<List<Category>> getCategories(@Query("page") int page, @Query("pageSize") int pageSize);
}
