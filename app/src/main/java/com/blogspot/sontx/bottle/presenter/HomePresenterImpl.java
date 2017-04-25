package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.bean.chat.CreateChannelResult;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerChatService;
import com.blogspot.sontx.bottle.presenter.interfaces.HomePresenter;
import com.blogspot.sontx.bottle.view.interfaces.HomeView;

import java.util.List;

public class HomePresenterImpl extends PresenterBase implements HomePresenter {
    private final HomeView homeView;
    private final BottleServerChatService bottleServerChatService;

    public HomePresenterImpl(HomeView homeView) {
        this.homeView = homeView;
        bottleServerChatService = FirebaseServicePool.getInstance().getBottleServerChatService();
    }

    @Override
    public void startChat(Channel channel) {
        List<ChannelMember> memberList = channel.getMemberList();
        if (memberList != null && memberList.size() > 1) {
            homeView.startChat(channel);
        }
    }

    @Override
    public void directMessage(RoomMessage roomMessage) {
        bottleServerChatService.createChannelAsync(roomMessage.getOwner().getId(), new Callback<CreateChannelResult>() {
            @Override
            public void onSuccess(CreateChannelResult result) {
                homeView.startChat(result.getId());
            }

            @Override
            public void onError(Throwable what) {
                homeView.showErrorMessage(what.getMessage());
            }
        });
    }
}
