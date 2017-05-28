package com.blogspot.sontx.bottle.model.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class QRData implements Serializable {
    private String id;
    private String message;
    private String channelId;
    private String currentUserId;
    private String anotherGuyId;
    private boolean read;
}
