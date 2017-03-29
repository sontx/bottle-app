package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.presenter.interfaces.HomePresenter;
import com.blogspot.sontx.bottle.view.interfaces.HomeView;

public class HomePresenterImpl implements HomePresenter {
    private final HomeView homeView;

    public HomePresenterImpl(HomeView homeView) {
        this.homeView = homeView;
    }
}
