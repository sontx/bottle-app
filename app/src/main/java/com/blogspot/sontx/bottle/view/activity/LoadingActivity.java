package com.blogspot.sontx.bottle.view.activity;

import android.os.Bundle;

import com.blogspot.sontx.bottle.R;

public class LoadingActivity extends ActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullscreen();
        setContentView(R.layout.activity_loading);
    }

}
