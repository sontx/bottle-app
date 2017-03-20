package com.blogspot.sontx.bottle.presenter.interfaces;

import android.graphics.Bitmap;

public interface WriteMessagePresenter {
    void setExtraAsPhoto(Bitmap bitmap);

    void removeExtra();

    boolean isContainsExtra();
}
