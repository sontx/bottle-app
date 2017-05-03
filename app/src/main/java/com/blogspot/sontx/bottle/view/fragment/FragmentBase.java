package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.blogspot.sontx.bottle.view.dialog.ProcessDialog;
import com.blogspot.sontx.bottle.view.interfaces.ViewBase;

import lombok.NonNull;

abstract class FragmentBase extends Fragment implements ViewBase {
    protected final static String TAG = "fragment";

    private ProcessDialog processDialog;

    protected void runOnUiThread(@NonNull Runnable runnable) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(runnable);
        }
    }

    @Override
    public void showProcess() {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (processDialog == null)
                        processDialog = new ProcessDialog(activity);
                    if (!processDialog.isShowing())
                        processDialog.show();
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideProcess();
    }

    @Override
    public void hideProcess() {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (processDialog != null) {
                        processDialog.hide();
                        processDialog = null;
                    }
                }
            });
        }
    }

    @Override
    public void showErrorMessage(final Object message) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (message instanceof String) {
                        Toast.makeText(activity, (String) message, Toast.LENGTH_LONG).show();
                    } else if (message instanceof Throwable) {
                        Throwable throwable = (Throwable) message;
                        String msg = throwable.getMessage();
                        if (msg == null || "".equals(msg))
                            msg = throwable.toString();
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, throwable.toString());
                    }
                }
            });
        }
    }

    @Override
    public void showSuccessMessage(final String message) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Nullable
    @Override
    public Context getContext() {
        return getActivity();
    }
}
