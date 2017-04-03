package com.blogspot.sontx.bottle.presenter;

import android.content.Intent;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.PrivateProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.HomePresenter;
import com.blogspot.sontx.bottle.system.event.ChangeCurrentUserEvent;
import com.blogspot.sontx.bottle.system.service.MessagingService;
import com.blogspot.sontx.bottle.view.interfaces.HomeView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class HomePresenterImpl extends PresenterBase implements HomePresenter {
    private final HomeView homeView;
    private final PrivateProfileService privateProfileService;

    public HomePresenterImpl(HomeView homeView) {
        this.homeView = homeView;
        privateProfileService = FirebaseServicePool.getInstance().getPrivateProfileService();
    }

    @Override
    public void switchCurrentUserId(String currentUserId) {
        FirebaseServicePool.getInstance().getChatService().setCurrentUserId(currentUserId);

        ChangeCurrentUserEvent changeCurrentUserEvent = new ChangeCurrentUserEvent();
        changeCurrentUserEvent.setNewCurrentUserId(currentUserId);
        EventBus.getDefault().post(changeCurrentUserEvent);
    }

    @Override
    public void startChat(Channel channel) {
        List<ChannelMember> memberList = channel.getMemberList();
        if (memberList != null && memberList.size() > 1) {
            homeView.startChat(channel, privateProfileService.getCurrentUserId());
        }
    }

    @Override
    public void onStart() {
        startChatService();
    }

    @Override
    public void onStop() {
    }

    private void startChatService() {
        if (!MessagingService.isRunning())
            homeView.getContext().startService(new Intent(homeView.getContext(), MessagingService.class));
    }
}
