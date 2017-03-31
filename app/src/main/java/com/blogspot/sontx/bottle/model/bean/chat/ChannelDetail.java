package com.blogspot.sontx.bottle.model.bean.chat;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

import lombok.Data;

@Data
public class ChannelDetail implements Serializable {
    @PropertyName("last_message")
    private String lastMessage;
    @PropertyName("message_type")
    private String messageType;
    @PropertyName("timestamp")
    private long timestamp;
}
