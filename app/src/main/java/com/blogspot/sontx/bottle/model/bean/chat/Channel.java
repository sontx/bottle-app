package com.blogspot.sontx.bottle.model.bean.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import lombok.Data;

@Data
public class Channel {
    private String id;
    private long timestamp;
    @JsonIgnore
    private ChannelDetail detail;
    @JsonIgnore
    private List<ChannelMember> memberList;
}
