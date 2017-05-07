package com.blogspot.sontx.bottle.model.bean;

import lombok.Getter;
import lombok.Setter;

public abstract class MessageBase {
    public static final String TEXT = "text";
    public static final String PHOTO = "photo";
    public static final String VIDEO = "video";
    public static final String RECORDING = "recording";
    public static final String LINK = "link";
    public static final int UNDEFINED_ID = -1;

    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    private String text;

    @Getter
    @Setter
    private String mediaUrl;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private long timestamp;

    @Getter
    @Setter
    private PublicProfile owner;
}
