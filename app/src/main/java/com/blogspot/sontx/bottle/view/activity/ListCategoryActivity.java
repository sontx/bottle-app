package com.blogspot.sontx.bottle.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.presenter.CategoryPresenterImpl;
import com.blogspot.sontx.bottle.presenter.RoomPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.CategoryPresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomPresenter;
import com.blogspot.sontx.bottle.view.fragment.ListCategoryFragment;
import com.blogspot.sontx.bottle.view.fragment.ListRoomFragment;
import com.blogspot.sontx.bottle.view.interfaces.ListCategoryView;
import com.blogspot.sontx.bottle.view.interfaces.ListRoomView;

import java.util.List;

public class ListCategoryActivity extends ActivityBase implements ListCategoryView, ListRoomView, ListCategoryFragment.OnListCategoryInteractionListener, ListRoomFragment.OnListRoomInteractionListener {
    private CategoryPresenter categoryPresenter;
    private RoomPresenter roomPresenter;
    private int fragmentStackIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryPresenter = new CategoryPresenterImpl(this);
        categoryPresenter.onCreate();

        roomPresenter = new RoomPresenterImpl(this);
    }

    @Override
    public void showCategories(List<Category> result) {
        ListCategoryFragment listCategoryFragment = ListCategoryFragment.newInstance(1);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, listCategoryFragment);
        fragmentTransaction.commit();

        listCategoryFragment.showCategories(result);
        fragmentStackIndex = 1;
    }

    @Override
    public void showRooms(List<Room> rooms) {
        ListRoomFragment listRoomFragment = ListRoomFragment.newInstance(1);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, listRoomFragment);
        fragmentTransaction.commit();

        listRoomFragment.showRooms(rooms);
        fragmentStackIndex++;
    }

    @Override
    public void onListCategoryInteraction(Category item) {
        roomPresenter.getRoomsAsync(item.getId());
    }

    @Override
    public void onListRoomInteraction(Room item) {

    }

    @Override
    public void onBackPressed() {
        if (fragmentStackIndex > 0) {
            categoryPresenter.onCreate();
            fragmentStackIndex--;
        } else {
            super.onBackPressed();
        }
    }
}
