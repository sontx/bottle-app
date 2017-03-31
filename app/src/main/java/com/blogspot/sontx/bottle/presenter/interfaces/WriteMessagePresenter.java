package com.blogspot.sontx.bottle.presenter.interfaces;

import android.graphics.Bitmap;

public interface WriteMessagePresenter extends ViewLifecyclePresenter {
    void setExtraAsPhoto(Bitmap bitmap);

    void removeExtra();

    boolean isContainsExtra();

    void setExtraAsVideo(String videoPath);
}
