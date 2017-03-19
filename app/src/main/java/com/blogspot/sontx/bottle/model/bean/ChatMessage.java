package com.blogspot.sontx.bottle.model.bean;

import lombok.Data;

@Data
public class ChatMessage {
    private String id;
    private String senderId;
    private String content;
    private String type;
    private long createdTime;
}
