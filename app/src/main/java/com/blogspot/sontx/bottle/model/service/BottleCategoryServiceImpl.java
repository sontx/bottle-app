package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleCategoryService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.rest.ApiCategory;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

class BottleCategoryServiceImpl extends BottleServerServiceBase implements BottleCategoryService {

    private final BottleServerAuthService bottleServerAuthService;

    BottleCategoryServiceImpl(BottleServerAuthService bottleServerAuthService) {
        this.bottleServerAuthService = bottleServerAuthService;
    }

    @Override
    public void getCategoriesAsync(final Callback<List<Category>> callback) {
        BottleUser bottleUser = bottleServerAuthService.getCurrentBottleUser();
        if (bottleUser == null || bottleUser.getToken() == null) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        ApiCategory apiCategory = ApiClient.getClient(bottleUser.getToken()).create(ApiCategory.class);

        Call<List<Category>> call = apiCategory.getCategories(0, 100);// assume that we want to get all categories

        call.enqueue(new retrofit2.Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "getCategoriesAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
