package com.blogspot.sontx.bottle.view.activity;

import android.content.Context;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.blogspot.sontx.bottle.view.dialog.ProcessDialog;
import com.blogspot.sontx.bottle.view.interfaces.ViewBase;

abstract class ActivityBase extends AppCompatActivity implements ViewBase {
    protected static final String TAG = "ACTIVITY";
    private ProcessDialog processDialog;

    protected void requestFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void adjustSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    protected void replaceFragment(int placeId, Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(placeId, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void addFragment(int placeId, Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(placeId, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @UiThread
    @Override
    public void showProcess() {
        if (processDialog == null)
            processDialog = new ProcessDialog(ActivityBase.this);
        if (!processDialog.isShowing())
            processDialog.show();
    }

    @UiThread
    @Override
    public void hideProcess() {
        if (processDialog != null)
            processDialog.hide();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (processDialog != null && processDialog.isShowing())
            processDialog.hide();
    }

    @UiThread
    @Override
    public void showErrorMessage(final String message) {
        Toast.makeText(ActivityBase.this, message, Toast.LENGTH_LONG).show();
    }

    @UiThread
    @Override
    public void showSuccessMessage(final String message) {
        Toast.makeText(ActivityBase.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
