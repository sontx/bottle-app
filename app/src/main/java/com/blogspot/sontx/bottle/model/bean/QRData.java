package com.blogspot.sontx.bottle.model.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class QRData implements Serializable {
    private String message;
    private String currentUserId;
    private String anotherGuyId;
}
