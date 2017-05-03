package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.bean.UploadResult;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleFileStreamService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomMessagePresenter;
import com.blogspot.sontx.bottle.utils.StringUtils;
import com.blogspot.sontx.bottle.view.interfaces.ListRoomMessageView;

import java.util.List;

public class RoomMessagePresenterImpl extends PresenterBase implements RoomMessagePresenter {
    private final ListRoomMessageView listRoomMessageView;
    private final BottleServerMessageService bottleServerMessageService;
    private final BottleServerRoomService bottleServerRoomService;
    private final BottleFileStreamService bottleFileStreamService;
    private final PublicProfile currentPublicProfile;
    private int currentPage = 0;
    private Room currentRoom;

    public RoomMessagePresenterImpl(ListRoomMessageView listRoomMessageView) {
        this.listRoomMessageView = listRoomMessageView;
        bottleServerMessageService = FirebaseServicePool.getInstance().getBottleServerMessageService();
        bottleServerRoomService = FirebaseServicePool.getInstance().getBottleServerRoomService();
        bottleFileStreamService = FirebaseServicePool.getInstance().getBottleFileStreamService();
        currentPublicProfile = App.getInstance().getBottleContext().getCurrentPublicProfile();
    }

    @Override
    public void setCurrentRoomId(int currentRoomId) {
        currentRoom = new Room();
        currentRoom.setId(currentRoomId);
        currentRoom.setCategoryId(-1);

        currentPage = 0;

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
        listRoomMessageView.clearRoomMessages();

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
    public void postRoomMessageAsync(String text, String mediaPath, String type) {
        final RoomMessage tempRoomMessage = new RoomMessage();
        tempRoomMessage.setText(text);
        tempRoomMessage.setOwner(currentPublicProfile);
        tempRoomMessage.setMediaUrl(mediaPath);
        tempRoomMessage.setType(type);

        if (currentPublicProfile != null)
            listRoomMessageView.addRoomMessage(tempRoomMessage);

        if (StringUtils.isEmpty(mediaPath)) {
            postRoomMessageAsync(tempRoomMessage);
        } else {
            // link, we don't need to upload them
            if (type.equalsIgnoreCase(MessageBase.LINK)) {
                postRoomMessageAsync(tempRoomMessage);
            } else {
                bottleFileStreamService.uploadAsync(mediaPath, new Callback<UploadResult>() {
                    @Override
                    public void onSuccess(UploadResult result) {
                        tempRoomMessage.setMediaUrl(result.getName());
                        postRoomMessageAsync(tempRoomMessage);
                    }

                    @Override
                    public void onError(Throwable what) {
                        listRoomMessageView.showErrorMessage(what.getMessage());
                    }
                });
            }
        }
    }

    private void postRoomMessageAsync(final RoomMessage tempRoomMessage) {
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

    @Override
    public void selectRoom(int roomId) {
        if (currentRoom == null || currentRoom.getId() != roomId) {
            setCurrentRoomId(roomId);
            getMoreRoomMessagesAsync();
        }
    }
}
