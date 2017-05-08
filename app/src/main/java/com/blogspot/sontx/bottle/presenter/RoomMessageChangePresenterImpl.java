package com.blogspot.sontx.bottle.presenter;

import android.util.Log;

import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.bean.event.RoomMessageChanged;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerRoomService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerStompService;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomMessageChangePresenter;
import com.blogspot.sontx.bottle.view.interfaces.RoomMessageChangeView;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RoomMessageChangePresenterImpl extends PresenterBase implements RoomMessageChangePresenter, SimpleCallback<String> {
    private static final String TOPIC_PREFIX = "/room/";

    private final BottleServerStompService bottleServerStompService;
    private final BottleServerRoomService bottleServerRoomService;
    private final RoomMessageChangeView roomMessageChangeView;
    private final ObjectMapper objectMapper;
    private String currentSubscribeTopic = null;
    private int currentRoomId = -1;

    public RoomMessageChangePresenterImpl(RoomMessageChangeView roomMessageChangeView) {
        this.roomMessageChangeView = roomMessageChangeView;
        bottleServerStompService = FirebaseServicePool.getInstance().getBottleServerStompService();
        bottleServerRoomService = FirebaseServicePool.getInstance().getBottleServerRoomService();
        objectMapper = new ObjectMapper();
    }

    @Override
    public void selectRoom(int roomId) {
        this.currentRoomId = roomId;
    }

    @Override
    public void subscribe() {
        if (currentRoomId < 0)
            return;

        String newTopic = getTopic();
        if (currentSubscribeTopic != null && !currentSubscribeTopic.equals(newTopic))
            bottleServerStompService.unsubscribe(currentSubscribeTopic);
        bottleServerStompService.subscribe(newTopic, this);
        currentSubscribeTopic = newTopic;
    }

    @Override
    public void unsubscribe() {
        if (currentSubscribeTopic != null)
            bottleServerStompService.unsubscribe(currentSubscribeTopic);
    }

    @Override
    public void onCallback(String value) {
        if (value != null && !"".equals(value)) {
            try {
                RoomMessageChanged roomMessageChanged = objectMapper.readValue(value, RoomMessageChanged.class);
                acceptRoomMessageChangeAsync(roomMessageChanged);
            } catch (IOException e) {
                roomMessageChangeView.showErrorMessage(e);
                Log.e(TAG, "onCallback", e);
            }
        }
    }

    private void acceptRoomMessageChangeAsync(final RoomMessageChanged roomMessageChanged) {
        if (!roomMessageChanged.getState().equals("delete")) {
            bottleServerRoomService.getRoomMessageAsync(roomMessageChanged.getId(), new Callback<RoomMessage>() {
                @Override
                public void onSuccess(RoomMessage result) {
                    String state = roomMessageChanged.getState();

                    if ("add".equals(state))
                        roomMessageChangeView.addRoomMessage(result);
                    else if ("update".equals(state))
                        roomMessageChangeView.updateRoomMessage(result);
                }

                @Override
                public void onError(Throwable what) {
                    roomMessageChangeView.showErrorMessage(what);
                }
            });
        } else {
            roomMessageChangeView.removeRoomMessage(roomMessageChanged.getId());
        }
    }

    private String getTopic() {
        return TOPIC_PREFIX + currentRoomId;
    }
}
