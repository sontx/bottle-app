package com.blogspot.sontx.bottle.model.bean.chat;

import lombok.Getter;

public enum MessageType {
    NONE("none"),
    TEXT("text"),
    MEDIA("media");

    @Getter
    private String type;

    MessageType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
