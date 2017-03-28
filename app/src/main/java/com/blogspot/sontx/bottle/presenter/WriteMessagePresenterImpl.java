package com.blogspot.sontx.bottle.presenter;

import android.graphics.Bitmap;

import com.blogspot.sontx.bottle.presenter.interfaces.WriteMessagePresenter;
import com.blogspot.sontx.bottle.view.interfaces.WriteMessageView;

import java.io.File;

public class WriteMessagePresenterImpl extends PresenterBase implements WriteMessagePresenter {
    private final WriteMessageView writeMessageView;
    private ExtraObject extraObject;

    public WriteMessagePresenterImpl(WriteMessageView writeMessageView) {
        this.writeMessageView = writeMessageView;
    }

    @Override
    public void setExtraAsPhoto(Bitmap bitmap) {
        freeLastExtraObjectResource();
        extraObject = new ExtraObject();
        extraObject.object = bitmap;
        extraObject.type = ExtraType.IMAGE;
    }

    @Override
    public void setExtraAsVideo(String videoPath) {
        freeLastExtraObjectResource();
        extraObject = new ExtraObject();
        extraObject.object = videoPath;
        extraObject.type = ExtraType.VIDEO;
    }

    @Override
    public void removeExtra() {
        freeLastExtraObjectResource();
    }

    @Override
    public boolean isContainsExtra() {
        return extraObject != null;
    }

    private void freeLastExtraObjectResource() {
        if (extraObject == null)
            return;
        if (extraObject.type == ExtraType.IMAGE)
            ((Bitmap) extraObject.object).recycle();
        else if (extraObject.type == ExtraType.VIDEO)
            new File(extraObject.object.toString()).delete();
        extraObject = null;
    }

    private enum ExtraType {
        IMAGE,
        VIDEO,
        RECORDING,
        LINK
    }

    private static class ExtraObject {
        private Object object;
        ExtraType type;
    }
}
