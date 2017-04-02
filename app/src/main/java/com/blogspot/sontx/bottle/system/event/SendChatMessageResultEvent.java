package com.blogspot.sontx.bottle.system.event;

import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;

import lombok.Data;

@Data
public class SendChatMessageResultEvent {
    private int id;
    private String channelId;
    private ChatMessage result;
}
