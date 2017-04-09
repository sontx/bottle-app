package com.blogspot.sontx.bottle.model.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoomMessage extends MessageBase {
    private int roomId;
}
