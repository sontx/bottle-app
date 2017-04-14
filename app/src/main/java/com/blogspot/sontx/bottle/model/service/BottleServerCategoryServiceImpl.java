package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerCategoryService;
import com.blogspot.sontx.bottle.model.service.rest.ApiCategory;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

class BottleServerCategoryServiceImpl extends BottleServerServiceBase implements BottleServerCategoryService {

    @Override
    public void getRoomsAsync(int categoryId, final Callback<List<Room>> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiCategory apiCategory = ApiClient.getClient(bottleUser.getToken()).create(ApiCategory.class);

        Call<List<Room>> call = apiCategory.getRooms(categoryId, 0, 100);// assume that we want to get all rooms

        call.enqueue(new retrofit2.Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "getRoomsAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public void getCategoriesAsync(final Callback<List<Category>> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

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

    @Override
    public void getCategoryAsync(int categoryId, final Callback<Category> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        ApiCategory apiCategory = ApiClient.getClient(bottleUser.getToken()).create(ApiCategory.class);

        Call<Category> call = apiCategory.getCategory(categoryId);

        call.enqueue(new retrofit2.Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "getCategoriesAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
