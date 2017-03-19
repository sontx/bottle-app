package com.blogspot.sontx.bottle.model.bean;

import lombok.Getter;

public enum ChatMessageType {
    TEXT("text"),
    MEDIA("media");

    @Getter
    private String type;

    ChatMessageType(String type) {
        this.type = type;
    }
}
