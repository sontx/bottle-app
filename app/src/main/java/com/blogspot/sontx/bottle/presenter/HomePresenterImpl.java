package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.presenter.interfaces.HomePresenter;
import com.blogspot.sontx.bottle.view.interfaces.HomeView;

import java.util.List;

public class HomePresenterImpl extends PresenterBase implements HomePresenter {
    private final HomeView homeView;

    public HomePresenterImpl(HomeView homeView) {
        this.homeView = homeView;
    }

    @Override
    public void startChat(Channel channel) {
        List<ChannelMember> memberList = channel.getMemberList();
        if (memberList != null && memberList.size() > 1) {
            homeView.startChat(channel);
        }
    }
}
