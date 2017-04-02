package com.blogspot.sontx.bottle.system.event;

import lombok.Data;

@Data
public class SendChatTextMessageEvent {
    private int id;
    private String channelId;
    private String text;
}
