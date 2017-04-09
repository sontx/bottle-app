package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomMessagePresenter;
import com.blogspot.sontx.bottle.view.interfaces.ListRoomMessageView;

import java.util.List;

import lombok.Setter;

public class RoomMessagePresenterImpl extends PresenterBase implements RoomMessagePresenter {
    private final ListRoomMessageView listRoomMessageView;
    private final BottleServerMessageService bottleServerMessageService;
    private int currentPage = 0;
    @Setter
    private int currentRoomId;

    public RoomMessagePresenterImpl(ListRoomMessageView listRoomMessageView) {
        this.listRoomMessageView = listRoomMessageView;
        bottleServerMessageService = FirebaseServicePool.getInstance().getBottleServerMessageService();
    }

    @Override
    public synchronized void getMoreRoomMessagesAsync() {
        bottleServerMessageService.getRoomMessages(currentRoomId, currentPage, 10, new Callback<List<RoomMessage>>() {
            @Override
            public void onSuccess(List<RoomMessage> result) {
                currentPage++;
                listRoomMessageView.showRoomMessages(result);
            }

            @Override
            public void onError(Throwable what) {
                listRoomMessageView.showErrorMessage(what.getMessage());
            }
        });
    }
}
