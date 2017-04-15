package com.blogspot.sontx.bottle.view.interfaces;

public interface WriteMessageView extends ViewBase {
    String getText();

    void goBackWithSuccess(String text, String mediaPath, String type);
}
