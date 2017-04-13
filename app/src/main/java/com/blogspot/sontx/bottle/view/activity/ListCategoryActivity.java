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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_category);
        placeListCategoryFragment();
    }

    private void placeListCategoryFragment() {
        ListCategoryFragment listCategoryFragment = ListCategoryFragment.newInstance(1);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, listCategoryFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onListCategoryInteraction(Category item) {

    }

    @Override
    public void onListRoomInteraction(Room item) {

    }
}
