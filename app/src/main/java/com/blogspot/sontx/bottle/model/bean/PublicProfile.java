package com.blogspot.sontx.bottle.model.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PublicProfile {
    @JsonIgnore
    private String id;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("display_name")
    private String displayName;
}
