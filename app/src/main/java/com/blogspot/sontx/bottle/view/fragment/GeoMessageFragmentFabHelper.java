package com.blogspot.sontx.bottle.view.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.blogspot.sontx.bottle.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.oguzdev.circularfloatingactionmenu.library.animation.DefaultAnimationHandler;

class GeoMessageFragmentFabHelper implements View.OnClickListener {
    private final Activity activity;
    private FloatingActionButton floatingActionButton;
    private final OnFabItemClickListener listener;
    private ImageView rlIcon1;
    private ImageView rlIcon2;
    private ImageView rlIcon3;
    private FloatingActionMenu floatingActionMenu;

    GeoMessageFragmentFabHelper(Activity activity, OnFabItemClickListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    void create() {
        ImageView fabContent = new ImageView(activity);
        fabContent.setImageResource(R.drawable.ic_more_horiz);

        floatingActionButton = new FloatingActionButton.Builder(activity)
                .setTheme(FloatingActionButton.THEME_LIGHT)
                .setContentView(fabContent)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_LEFT)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(activity)
                .setTheme(SubActionButton.THEME_LIGHT);

        rlIcon1 = new ImageView(activity);
        rlIcon2 = new ImageView(activity);
        rlIcon3 = new ImageView(activity);

        rlIcon1.setImageResource(R.drawable.ic_my_location);
        rlIcon2.setImageResource(R.drawable.ic_location_on);
        rlIcon3.setImageResource(R.drawable.ic_insert_emoticon);

        floatingActionMenu = new FloatingActionMenu.Builder(activity)
                .setStartAngle(0)
                .setEndAngle(-90)
                .setAnimationHandler(new DefaultAnimationHandler())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .attachTo(floatingActionButton)
                .build();

        rlIcon1.setOnClickListener(this);
        rlIcon2.setOnClickListener(this);
        rlIcon3.setOnClickListener(this);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.setVisibility(View.GONE);
            floatingActionMenu.close(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (listener == null)
            return;
        if (v == rlIcon1)
            listener.onFabMyDeviceLocationClick();
        else if (v == rlIcon2)
            listener.onFabMyLocationClick();
        else if (v == rlIcon3)
            listener.onFabMyGeoMessageLocationClick();
    }

    interface OnFabItemClickListener {
        void onFabMyLocationClick();

        void onFabMyDeviceLocationClick();

        void onFabMyGeoMessageLocationClick();
    }
}
