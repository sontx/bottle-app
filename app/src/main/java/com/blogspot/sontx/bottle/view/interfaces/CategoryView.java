package com.blogspot.sontx.bottle.view.interfaces;


import com.blogspot.sontx.bottle.model.bean.Category;

import java.util.List;

public interface CategoryView extends ViewBase {
    void showCategories(List<Category> result);
}
