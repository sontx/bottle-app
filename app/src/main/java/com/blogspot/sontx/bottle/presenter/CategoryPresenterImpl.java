package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerCategoryService;
import com.blogspot.sontx.bottle.presenter.interfaces.CategoryPresenter;
import com.blogspot.sontx.bottle.view.interfaces.ListCategoryView;

import java.util.List;

public class CategoryPresenterImpl extends PresenterBase implements CategoryPresenter {
    private final BottleServerCategoryService bottleServerCategoryService;
    private final ListCategoryView listCategoryView;

    public CategoryPresenterImpl(ListCategoryView listCategoryView) {
        this.listCategoryView = listCategoryView;
        this.bottleServerCategoryService = FirebaseServicePool.getInstance().getBottleServerCategoryService();
    }

    @Override
    public void getCategoriesAsync() {
        bottleServerCategoryService.getCategoriesAsync(new Callback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                listCategoryView.showCategories(result);
            }

            @Override
            public void onError(Throwable what) {
                listCategoryView.showErrorMessage(what);
            }
        });
    }
}
