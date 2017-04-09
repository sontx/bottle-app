package com.blogspot.sontx.bottle.model.bean.chat;

import android.support.annotation.Nullable;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.system.BottleContext;
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

    @Exclude
    @Nullable
    public ChannelMember getCurrentUser() {
        if (memberList == null)
            return null;

        BottleContext bottleContext = App.getInstance().getBottleContext();
        if (!bottleContext.isLogged())
            return null;

        BottleUser currentBottleUser = bottleContext.getCurrentBottleUser();
        for (ChannelMember member : memberList) {
            if (member.getId().equals(currentBottleUser.getUid()))
                return member;
        }

        return null;
    }

    @Exclude
    @Nullable
    public ChannelMember getAnotherGuy() {
        if (memberList == null)
            return null;

        BottleContext bottleContext = App.getInstance().getBottleContext();
        if (!bottleContext.isLogged())
            return null;

        BottleUser currentBottleUser = bottleContext.getCurrentBottleUser();
        for (ChannelMember member : memberList) {
            if (!member.getId().equals(currentBottleUser.getUid()))
                return member;
        }

        return null;
    }
}
