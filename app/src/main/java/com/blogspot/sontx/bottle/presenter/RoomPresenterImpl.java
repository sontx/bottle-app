package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerCategoryService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomPresenter;
import com.blogspot.sontx.bottle.view.interfaces.ListRoomView;

import java.util.List;

public class RoomPresenterImpl extends PresenterBase implements RoomPresenter {
    private final ListRoomView listRoomView;
    private final BottleServerRoomService bottleServerRoomService;
    private final BottleServerCategoryService bottleServerCategoryService;

    public RoomPresenterImpl(ListRoomView listRoomView) {
        this.listRoomView = listRoomView;
        bottleServerRoomService = FirebaseServicePool.getInstance().getBottleServerRoomService();
        bottleServerCategoryService = FirebaseServicePool.getInstance().getBottleServerCategoryService();
    }

    @Override
    public void getRoomsAsync(int categoryId, boolean needCategory) {
        bottleServerCategoryService.getRoomsAsync(categoryId, new Callback<List<Room>>() {
            @Override
            public void onSuccess(List<Room> result) {
                listRoomView.showRooms(result);
            }

            @Override
            public void onError(Throwable what) {
                listRoomView.showErrorMessage(what);
            }
        });

        if (!needCategory)
            return;

        bottleServerCategoryService.getCategoryAsync(categoryId, new Callback<Category>() {
            @Override
            public void onSuccess(Category result) {
                listRoomView.showCategory(result);
            }

            @Override
            public void onError(Throwable what) {
                listRoomView.showErrorMessage(what);
            }
        });
    }

    @Override
    public void getRoomsHaveSameCategoryAsync(int roomId, final boolean needCategory) {
        bottleServerRoomService.getRoomAsync(roomId, new Callback<Room>() {
            @Override
            public void onSuccess(Room result) {
                getRoomsAsync(result.getCategoryId(), needCategory);
            }

            @Override
            public void onError(Throwable what) {
                listRoomView.showErrorMessage(what);
            }
        });
    }
}
