package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.utils.StringUtils;
import com.blogspot.sontx.bottle.view.fragment.ListRoomFragment;
import com.squareup.picasso.Picasso;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Provide either room id or category id. If provide room id, api will fetch category id first.
 */
public class ListRoomActivity extends ActivityBase implements ListRoomFragment.OnListRoomInteractionListener {

    public static final String ROOM_ID = "room-id";
    public static final String CATEGORY_ID = "category-id";

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.header_image)
    ImageView headerImageView;
    @BindDimen(R.dimen.header_image_height)
    int previewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_room);
        setupToolbar();
        ButterKnife.bind(this);
        processArguments();
    }

    private void processArguments() {
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void placeListRoomFragmentByRoomId(int roomId) {
        ListRoomFragment listRoomFragment = ListRoomFragment.newInstanceWithRoomId(1, roomId);
        placeListRoomFragment(listRoomFragment);
    }

    private void placeListRoomFragmentByCategoryId(int categoryId) {
        ListRoomFragment listRoomFragment = ListRoomFragment.newInstanceWithCategoryId(1, categoryId);
        placeListRoomFragment(listRoomFragment);
    }

    private void placeListRoomFragment(ListRoomFragment listRoomFragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, listRoomFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onListRoomInteraction(Room item) {

    }

    @Override
    public void onCategoryAvailable(Category category) {
        collapsingToolbarLayout.setTitle(category.getName());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        if (!StringUtils.isEmpty(category.getPhotoUrl())) {
            int width = headerImageView.getWidth();
            Picasso.with(this).load(category.getPhotoUrl()).resize(width, previewHeight).centerCrop().into(headerImageView);
        }
    }
}
