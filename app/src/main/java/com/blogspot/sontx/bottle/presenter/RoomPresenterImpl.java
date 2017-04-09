package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomPresenter;
import com.blogspot.sontx.bottle.view.interfaces.ListRoomView;

import java.util.List;

public class RoomPresenterImpl extends PresenterBase implements RoomPresenter {
    private final ListRoomView listRoomView;
    private final BottleServerRoomService bottleServerRoomService;

    public RoomPresenterImpl(ListRoomView listRoomView) {
        this.listRoomView = listRoomView;
        bottleServerRoomService = FirebaseServicePool.getInstance().getBottleServerRoomService();
    }

    @Override
    public void getRoomsAsync(int categoryId) {
        bottleServerRoomService.getRoomsAsync(categoryId, new Callback<List<Room>>() {
            @Override
            public void onSuccess(List<Room> result) {
                listRoomView.showRooms(result);
            }

            @Override
            public void onError(Throwable what) {
                listRoomView.showErrorMessage(what.getMessage());
            }
        });
    }
}
