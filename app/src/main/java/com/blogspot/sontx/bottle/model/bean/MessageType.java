package com.blogspot.sontx.bottle.model.bean;

import lombok.Getter;

public enum MessageType {
    TEXT("text"),
    MEDIA("media");

    @Getter
    private String type;

    private MessageType(String type) {
        this.type = type;
    }
}
