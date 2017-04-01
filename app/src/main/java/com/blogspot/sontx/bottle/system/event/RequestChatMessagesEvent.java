package com.blogspot.sontx.bottle.system.event;

import lombok.Data;

@Data
public class RequestChatMessagesEvent {
    private String channelId;
    private long startAtTimestamp;
    private int limit;
}
