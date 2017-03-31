package com.blogspot.sontx.bottle.system.event;

import lombok.Data;

@Data
public class ChatMessageResponseEvent {
    private int id;
    private String channelId;
    private String result;
}
