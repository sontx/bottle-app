package com.blogspot.sontx.bottle.model.bean.chat;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ChannelMember {
    private String id;
    private long timestamp;
    @JsonIgnore
    private PublicProfile publicProfile;
}
