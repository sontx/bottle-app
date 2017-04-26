package com.blogspot.sontx.bottle.system.event;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;

import lombok.Data;

@Data
public class ChatChannelChangedEvent {
    private Channel channel;
}
