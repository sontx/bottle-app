package com.blogspot.sontx.bottle.system.event;

import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;

import java.util.List;

import lombok.Data;

@Data
public class ResponseChatMessagesEvent {
    private String channelId;
    private List<ChatMessage> chatMessageList;
}
