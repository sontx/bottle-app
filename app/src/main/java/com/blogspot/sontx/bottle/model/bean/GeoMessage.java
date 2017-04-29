package com.blogspot.sontx.bottle.model.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GeoMessage extends MessageBase {
    private double latitude;
    private double longitude;
    private String addressName;
}
