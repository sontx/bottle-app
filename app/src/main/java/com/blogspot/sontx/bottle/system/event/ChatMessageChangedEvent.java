package com.blogspot.sontx.bottle.system.event;

import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;

import lombok.Data;

@Data
public class ChatMessageChangedEvent {
    private ChatMessage chatMessage;
}
