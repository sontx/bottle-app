package com.blogspot.sontx.bottle.presenter.interfaces;

public interface WriteMessagePresenter {
    void setMediaAsPhoto(String photoPath);

    void setMediaAsVideo(String videoPath);

    void removeMedia();

    boolean isContainsMedia();

    void requestPostMessage();
}
