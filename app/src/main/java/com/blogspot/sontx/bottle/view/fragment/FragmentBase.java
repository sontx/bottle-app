package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.blogspot.sontx.bottle.view.dialog.ProcessDialog;
import com.blogspot.sontx.bottle.view.interfaces.ViewBase;

abstract class FragmentBase extends Fragment implements ViewBase {
    protected final static String TAG = "fragment";

    private ProcessDialog processDialog;

    @UiThread
    @Override
    public void showProcess() {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            if (processDialog == null)
                processDialog = new ProcessDialog(activity);
            if (!processDialog.isShowing())
                processDialog.show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideProcess();
    }

    @UiThread
    @Override
    public void hideProcess() {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            if (processDialog != null) {
                processDialog.hide();
                processDialog = null;
            }
        }
    }

    @UiThread
    @Override
    public void showErrorMessage(final String message) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        }
    }

    @UiThread
    @Override
    public void showSuccessMessage(final String message) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public Context getContext() {
        return getActivity();
    }
}
