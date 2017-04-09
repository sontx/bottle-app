package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleCategoryService;
import com.blogspot.sontx.bottle.presenter.interfaces.CategoryPresenter;
import com.blogspot.sontx.bottle.view.interfaces.CategoryView;

import java.util.List;

public class CategoryPresenterImpl extends PresenterBase implements CategoryPresenter {
    private final BottleCategoryService bottleCategoryService;
    private final CategoryView categoryView;

    public CategoryPresenterImpl(CategoryView categoryView) {
        this.categoryView = categoryView;
        this.bottleCategoryService = FirebaseServicePool.getInstance().getBottleCategoryService();
    }

    @Override
    public void onCreate() {
        getCategoriesAsync();
    }

    private void getCategoriesAsync() {
        categoryView.showProcess();
        bottleCategoryService.getCategoriesAsync(new Callback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                categoryView.hideProcess();
                categoryView.showCategories(result);
            }

            @Override
            public void onError(Throwable what) {
                categoryView.hideProcess();
                categoryView.showErrorMessage(what.getMessage());
            }
        });
    }
}
