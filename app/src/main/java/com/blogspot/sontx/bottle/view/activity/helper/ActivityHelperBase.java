package com.blogspot.sontx.bottle.view.activity.helper;

import android.content.res.Resources;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

abstract class ActivityHelperBase {
    private AppCompatActivity appCompatActivity;

    public ActivityHelperBase(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    FragmentManager getSupportFragmentManager() {
        return appCompatActivity.getSupportFragmentManager();
    }

    Resources getResources() {
        return appCompatActivity.getResources();
    }

    View findViewById(int id) {
        return appCompatActivity.findViewById(id);
    }
}
