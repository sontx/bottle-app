package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.service.Callback;

import java.util.List;

public interface BottleCategoryService extends ServiceBase {
    void getCategoriesAsync(Callback<List<Category>> callback);
}
