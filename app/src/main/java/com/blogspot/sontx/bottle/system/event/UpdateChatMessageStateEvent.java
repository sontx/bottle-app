package com.blogspot.sontx.bottle.system.event;

import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;

import java.util.List;

import lombok.Data;

@Data
public class UpdateChatMessageStateEvent {
    private List<ChatMessage> chatMessageList;
    private String newState;
}
