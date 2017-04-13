package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.bean.RoomList;
import com.blogspot.sontx.bottle.model.service.Callback;

import java.util.List;

public interface BottleServerCategoryService extends ServiceBase {
    void getRoomsAsync(int categoryId, Callback<RoomList> callback);

    void getCategoriesAsync(Callback<List<Category>> callback);

    void getCategoryAsync(int categoryId, Callback<Category> callback);
}
