package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomMessagePresenter;
import com.blogspot.sontx.bottle.view.interfaces.ListRoomMessageView;

import java.util.List;

public class RoomMessagePresenterImpl extends PresenterBase implements RoomMessagePresenter {
    private final ListRoomMessageView listRoomMessageView;
    private final BottleServerMessageService bottleServerMessageService;
    private final BottleServerRoomService bottleServerRoomService;
    private final PublicProfileService publicProfileService;
    private int currentPage = 0;
    private Room currentRoom;
    private PublicProfile currentPublicProfile;

    public RoomMessagePresenterImpl(ListRoomMessageView listRoomMessageView) {
        this.listRoomMessageView = listRoomMessageView;
        bottleServerMessageService = FirebaseServicePool.getInstance().getBottleServerMessageService();
        bottleServerRoomService = FirebaseServicePool.getInstance().getBottleServerRoomService();
        publicProfileService = FirebaseServicePool.getInstance().getPublicProfileService();
        getCurrentPublicProfileAsync();
    }

    private void getCurrentPublicProfileAsync() {
        String currentUserId = App.getInstance().getBottleContext().getCurrentBottleUser().getUid();
        publicProfileService.getPublicProfileAsync(currentUserId, new Callback<PublicProfile>() {
            @Override
            public void onSuccess(PublicProfile result) {
                currentPublicProfile = result;
            }

            @Override
            public void onError(Throwable what) {
                listRoomMessageView.showErrorMessage(what.getMessage());
            }
        });
    }

    @Override
    public void setCurrentRoomId(int currentRoomId) {
        currentRoom = new Room();
        currentRoom.setId(currentRoomId);
        currentRoom.setCategoryId(-1);

        bottleServerRoomService.getRoomAsync(currentRoomId, new Callback<Room>() {
            @Override
            public void onSuccess(Room result) {
                currentRoom = result;
            }

            @Override
            public void onError(Throwable what) {
                listRoomMessageView.showErrorMessage(what.getMessage());
            }
        });
    }

    @Override
    public synchronized void getMoreRoomMessagesAsync() {
        bottleServerMessageService.getRoomMessages(currentRoom.getId(), currentPage, 10, new Callback<List<RoomMessage>>() {
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

    @Override
    public void postRoomMessageAsync(String text, String mediaPath) {
        final RoomMessage tempRoomMessage = new RoomMessage();
        tempRoomMessage.setText(text);
        tempRoomMessage.setOwner(currentPublicProfile);

        if (currentPublicProfile != null)
            listRoomMessageView.addRoomMessage(tempRoomMessage);

        bottleServerRoomService.postRoomMessageAsync(currentRoom.getId(), tempRoomMessage, new Callback<RoomMessage>() {
            @Override
            public void onSuccess(RoomMessage result) {
                listRoomMessageView.updateRoomMessage(result, tempRoomMessage);
            }

            @Override
            public void onError(Throwable what) {
                listRoomMessageView.showErrorMessage(what.getMessage());
            }
        });
    }

    @Override
    public void jumpToListRooms() {
        if (currentRoom != null) {
            if (currentRoom.getCategoryId() >= 0)
                listRoomMessageView.showListRoomsByCategoryId(currentRoom.getCategoryId());
            else
                listRoomMessageView.showListRoomsByRoomId(currentRoom.getId());
        }
    }
}
