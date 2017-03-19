package com.blogspot.sontx.bottle.view.interfaces;

import it.slyce.messaging.message.Message;

public interface ChatView extends ViewBase {
    void displayMessage(Message result);
}
