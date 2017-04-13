package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.view.fragment.ListRoomFragment;

/**
 * Provide either room id or category id. If provide room id, api will fetch category id first.
 */
public class ListRoomActivity extends ActivityBase implements ListRoomFragment.OnListRoomInteractionListener {

    public static final String ROOM_ID = "room-id";
    public static final String CATEGORY_ID = "category-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_room);

        setupToolbar();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(ROOM_ID)) {
                int roomId = intent.getIntExtra(ROOM_ID, -1);
                placeListRoomFragmentByRoomId(roomId);
            } else if (intent.hasExtra(CATEGORY_ID)) {
                int categoryId = intent.getIntExtra(CATEGORY_ID, -1);
                placeListRoomFragmentByCategoryId(categoryId);
            } else {
                finish();
            }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void placeListRoomFragmentByRoomId(int roomId) {
        ListRoomFragment listRoomFragment = ListRoomFragment.newInstanceWithRoomId(1, roomId);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, listRoomFragment);
        fragmentTransaction.commit();
    }

    private void placeListRoomFragmentByCategoryId(int categoryId) {
        ListRoomFragment listRoomFragment = ListRoomFragment.newInstanceWithCategoryId(1, categoryId);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, listRoomFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onListRoomInteraction(Room item) {

    }
}
