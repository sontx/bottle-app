package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.presenter.CategoryPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.CategoryPresenter;
import com.blogspot.sontx.bottle.utils.StringUtils;
import com.blogspot.sontx.bottle.view.adapter.CategoryAdapter;
import com.blogspot.sontx.bottle.view.fragment.ListRoomFragment;
import com.blogspot.sontx.bottle.view.interfaces.ListCategoryView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Provide either room id or category id. If provide room id, api will fetch category id first.
 */
public class ListRoomActivity extends ActivityBase implements ListRoomFragment.OnListRoomInteractionListener, ListCategoryView {

    public static final String ROOM_ID = "room-id";
    public static final String CATEGORY_ID = "category-id";

    private CategoryPresenter categoryPresenter;

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
        categoryPresenter = new CategoryPresenterImpl(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = getIntent();
            setResult(RESULT_CANCELED, intent);
            finish();
            return true;
        }

        if (item.getItemId() == R.id.menu_item_category) {
            categoryPresenter.getCategoriesAsync();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        Intent intent = getIntent();
        intent.putExtra(ROOM_ID, item.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCategoryAvailable(Category category) {
        collapsingToolbarLayout.setTitle(category.getName());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        if (!StringUtils.isEmpty(category.getPhotoUrl())) {
            int width = headerImageView.getWidth();
            String url = resource.absoluteUrl(category.getPhotoUrl());
            Picasso.with(this).load(url).resize(width, previewHeight).centerCrop().into(headerImageView);
        }
    }

    @Override
    public void showCategories(List<Category> result) {
        DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(new CategoryAdapter(result))
                .setExpanded(true)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        headerImageView.setImageResource(R.drawable.default_cover);
                        placeListRoomFragmentByCategoryId(((Category) item).getId());
                        dialog.dismiss();
                    }
                })
                .create();
        dialogPlus.show();
    }
}
