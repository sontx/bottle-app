package com.blogspot.sontx.bottle.model.bean.chat;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class Channel implements Serializable {
    private String id;
    private long timestamp;
    @Exclude
    private ChannelDetail detail;
    @Exclude
    private List<ChannelMember> memberList;
}
