package com.blogspot.sontx.bottle.model.bean.chat;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

import lombok.Data;

@Data
public class ChannelMember implements Serializable {
    private String id;
    private long timestamp;
    @Exclude
    private PublicProfile publicProfile;
}
