package com.blogspot.sontx.bottle.model.bean.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ChannelDetail {
    @JsonProperty("last_message")
    private String lastMessage;
    @JsonProperty("message_type")
    private String messageType;
    @JsonProperty("timestamp")
    private long timestamp;
}
