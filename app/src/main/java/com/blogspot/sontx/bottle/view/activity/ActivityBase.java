package com.blogspot.sontx.bottle.view.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.system.Resource;
import com.blogspot.sontx.bottle.view.dialog.ProcessDialog;
import com.blogspot.sontx.bottle.view.interfaces.ViewBase;

abstract class ActivityBase extends AppCompatActivity implements ViewBase {
    protected static final String TAG = "ACTIVITY";
    private ProcessDialog processDialog;
    protected Resource resource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resource = App.getInstance().getBottleContext().getResource();
    }

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

    @Override
    public void showProcess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (processDialog == null)
                    processDialog = new ProcessDialog(ActivityBase.this);
                if (!processDialog.isShowing())
                    processDialog.show();
            }
        });
    }

    @Override
    public void hideProcess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (processDialog != null)
                    processDialog.hide();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (processDialog != null && processDialog.isShowing())
            processDialog.hide();
    }

    @Override
    public void showErrorMessage(final Object message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message instanceof String) {
                    Toast.makeText(ActivityBase.this, (String) message, Toast.LENGTH_LONG).show();
                } else if (message instanceof Throwable) {
                    Throwable throwable = (Throwable) message;
                    String msg = throwable.getMessage();
                    if (msg == null || "".equals(msg))
                        msg = throwable.toString();
                    Toast.makeText(ActivityBase.this, msg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, throwable.toString());
                }
            }
        });
    }

    @Override
    public void showSuccessMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ActivityBase.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    protected void changeStatusBarColor(int colorId) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(colorId));
        }
    }
}
