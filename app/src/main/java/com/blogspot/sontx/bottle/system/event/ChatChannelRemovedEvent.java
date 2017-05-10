package com.blogspot.sontx.bottle.system.event;

import lombok.Data;

@Data
public class ChatChannelRemovedEvent {
    private String channelId;
}
