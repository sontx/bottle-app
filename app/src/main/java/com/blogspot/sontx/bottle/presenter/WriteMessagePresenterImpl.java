package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.dummy.DummyEmotions;
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
        mediaObject.type = MessageBase.PHOTO;
    }

    @Override
    public void setMediaAsVideo(String videoPath) {
        freeLastExtraObjectResource();
        mediaObject = new MediaObject();
        mediaObject.resourcePath = videoPath;
        mediaObject.type = MessageBase.VIDEO;
    }

    @Override
    public void requestPostMessage() {
        String text = writeMessageView.getText();
        int emotion = writeMessageView.getEmotion();
        if (text == null || (text.trim()).length() == 0) {
            writeMessageView.showErrorMessage(writeMessageView.getContext().getString(R.string.message_text_empty));
        } else if (emotion >= 0 || emotion < DummyEmotions.getEmotions().size()) {
            String type = mediaObject != null ? mediaObject.type : MessageBase.TEXT;
            writeMessageView.goBackWithSuccess(text.trim(), mediaObject != null ? mediaObject.resourcePath : null, type, emotion);
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

    private static class MediaObject {
        String resourcePath;
        String type;
    }
}
