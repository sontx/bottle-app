package com.blogspot.sontx.bottle.model.bean;

import java.util.List;

import lombok.Data;

@Data
public class ChatMessages {
    private List<ChatMessage> messages;
}
