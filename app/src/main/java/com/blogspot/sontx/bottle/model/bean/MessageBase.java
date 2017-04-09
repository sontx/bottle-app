package com.blogspot.sontx.bottle.model.bean;

import lombok.Data;

@Data
public abstract class MessageBase {
    private int id;
    private String text;
    private String mediaUrl;
    private long timestamp;
    private PublicProfile owner;
}
