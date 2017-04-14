package com.blogspot.sontx.bottle.view.interfaces;

import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.bean.Room;

import java.util.List;

public interface ListRoomView extends ViewBase {
    void showRooms(List<Room> rooms);

    void showCategory(Category category);
}
