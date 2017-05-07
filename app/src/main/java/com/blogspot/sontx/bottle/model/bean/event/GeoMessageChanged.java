package com.blogspot.sontx.bottle.model.bean.event;

import lombok.Data;

@Data
public class GeoMessageChanged {
    private int id;
    private double latitude;
    private double longitude;
    private String state;
}
