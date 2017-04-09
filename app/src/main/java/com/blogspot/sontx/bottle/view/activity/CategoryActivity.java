package com.blogspot.sontx.bottle.view.activity;

import android.os.Bundle;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.presenter.CategoryPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.CategoryPresenter;
import com.blogspot.sontx.bottle.view.fragment.CategoryFragment;
import com.blogspot.sontx.bottle.view.interfaces.CategoryView;

import java.util.List;

public class CategoryActivity extends ActivityBase implements CategoryView, CategoryFragment.OnListCategoryInteractionListener {
    private CategoryPresenter categoryPresenter;
    private CategoryFragment categoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryPresenter = new CategoryPresenterImpl(this);
        categoryPresenter.onCreate();

        categoryFragment = CategoryFragment.newInstance(1);
        replaceFragment(R.id.content_fragment, categoryFragment);
    }

    @Override
    public void showCategories(List<Category> result) {
        categoryFragment.showCategories(result);
    }

    @Override
    public void onListCategoryInteraction(Category item) {

    }
}
