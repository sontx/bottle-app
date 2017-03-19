package com.blogspot.sontx.bottle.model.bean;

import lombok.Data;

@Data
public class ChatChannelInfo {
    private long lastActiveTime;
    private long createTime;
    private String recipientDisplayName;
    private String recipientAvatarUrl;
}
