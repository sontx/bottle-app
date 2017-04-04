package com.blogspot.sontx.bottle.model.bean;

import lombok.Data;

@Data
public class LoginData {
    public static final int STATE_SUCCESS = 0;
    public static final int STATE_CANCEL = 1;
    public static final int STATE_ERROR = 2;
    private int state;
    private String token;
    private String secret;
}
