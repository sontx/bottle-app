package com.blogspot.sontx.bottle.view.interfaces;

import android.content.Context;

public interface ViewBase {
    void showProcess();

    void hideProcess();

    void showErrorMessage(String message);

    void showSuccessMessage(String message);

    Context getContext();
}
