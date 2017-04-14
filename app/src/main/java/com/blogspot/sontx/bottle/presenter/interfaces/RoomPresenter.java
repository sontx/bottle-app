package com.blogspot.sontx.bottle.presenter.interfaces;

public interface RoomPresenter {
    void getRoomsAsync(int categoryId, boolean needCategory);

    void getRoomsHaveSameCategoryAsync(int roomId, boolean needCategory);
}
