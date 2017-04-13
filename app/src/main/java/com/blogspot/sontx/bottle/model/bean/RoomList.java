package com.blogspot.sontx.bottle.model.bean;

import java.util.List;

import lombok.Data;

@Data
public class RoomList {
    private int categoryId;
    private String categoryName;
    private List<Room> rooms;
}
