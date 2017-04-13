package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.presenter.interfaces.WriteMessagePresenter;
import com.blogspot.sontx.bottle.view.interfaces.WriteMessageView;

import java.io.File;

public class WriteMessagePresenterImpl extends PresenterBase implements WriteMessagePresenter {
    private final WriteMessageView writeMessageView;
    private MediaObject mediaObject;

    public WriteMessagePresenterImpl(WriteMessageView writeMessageView) {
        this.writeMessageView = writeMessageView;
    }

    @Override
    public void setMediaAsPhoto(String photoPath) {
        freeLastExtraObjectResource();
        mediaObject = new MediaObject();
        mediaObject.resourcePath = photoPath;
        mediaObject.type = MediaType.IMAGE;
    }

    @Override
    public void setMediaAsVideo(String videoPath) {
        freeLastExtraObjectResource();
        mediaObject = new MediaObject();
        mediaObject.resourcePath = videoPath;
        mediaObject.type = MediaType.VIDEO;
    }

    @Override
    public void requestPostMessage() {
        String text = writeMessageView.getText();
        if (text == null || (text.trim()).length() == 0) {
            writeMessageView.showErrorMessage(writeMessageView.getContext().getString(R.string.message_text_empty));
        } else {
            writeMessageView.goBackWithSuccess(text.trim(), mediaObject.resourcePath);
        }
    }

    @Override
    public void removeMedia() {
        freeLastExtraObjectResource();
    }

    @Override
    public boolean isContainsMedia() {
        return mediaObject != null;
    }

    private void freeLastExtraObjectResource() {
        if (mediaObject == null)
            return;

        new File(mediaObject.resourcePath).delete();

        mediaObject = null;
    }

    private enum MediaType {
        IMAGE,
        VIDEO,
        RECORDING,
        LINK
    }

    private static class MediaObject {
        String resourcePath;
        MediaType type;
    }
}
