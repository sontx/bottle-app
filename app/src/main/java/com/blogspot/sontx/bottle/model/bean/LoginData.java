package com.blogspot.sontx.bottle.model.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class LoginData {
    public static final int STATE_SUCCESS = 0;
    public static final int STATE_CANCEL = 1;
    public static final int STATE_ERROR = 2;
    @JsonIgnore
    private int state;
    private String token;
    private String secret;
}
