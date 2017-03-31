package com.blogspot.sontx.bottle.model.bean;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;

import lombok.Data;

@Data
public class PublicProfile implements Serializable {
    @Exclude
    private String id;
    @PropertyName("avatar_url")
    private String avatarUrl;
    @PropertyName("display_name")
    private String displayName;
}
