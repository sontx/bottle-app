package com.blogspot.sontx.bottle.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.view.fragment.ListCategoryFragment;
import com.blogspot.sontx.bottle.view.fragment.ListRoomFragment;

public class ListCategoryActivity extends ActivityBase implements ListCategoryFragment.OnListCategoryInteractionListener, ListRoomFragment.OnListRoomInteractionListener {

    private int fragmentStackIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        placeCategoryFragment();
    }

    private void placeCategoryFragment() {
        ListCategoryFragment listCategoryFragment = ListCategoryFragment.newInstance(1);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, listCategoryFragment);
        fragmentTransaction.commit();

        fragmentStackIndex = 1;
    }

    @Override
    public void onListCategoryInteraction(Category item) {
        ListRoomFragment listRoomFragment = ListRoomFragment.newInstance(1, item.getId());

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, listRoomFragment);
        fragmentTransaction.commit();

        fragmentStackIndex++;
    }

    @Override
    public void onListRoomInteraction(Room item) {

    }

    @Override
    public void onBackPressed() {
        if (fragmentStackIndex > 0) {
            placeCategoryFragment();
            fragmentStackIndex--;
        } else {
            super.onBackPressed();
        }
    }
}
