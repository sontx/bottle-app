package com.blogspot.sontx.bottle.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.blogspot.sontx.bottle.R;

public class ProcessDialog extends DialogBase {
    private final Dialog dialog;

    public ProcessDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_process);
        dialog.setCancelable(false);
    }

    @Override
    public void show() {
        dialog.show();
    }

    @Override
    public void hide() {
        dialog.dismiss();
    }

    @Override
    public boolean isShowing() {
        return dialog.isShowing();
    }
}
