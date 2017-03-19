package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.ChatChannelInfo;
import com.blogspot.sontx.bottle.model.service.FirebaseChatChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.Callback;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatChannelService;
import com.blogspot.sontx.bottle.presenter.interfaces.ChatChannelPresenter;
import com.blogspot.sontx.bottle.view.interfaces.ChatChannelView;

public class ChatChannelPresenterImpl extends PresenterBase implements ChatChannelPresenter {

    private final ChatChannelView chatChannelView;
    private final ChatChannelService chatChannelService;

    public ChatChannelPresenterImpl(final ChatChannelView chatChannelView) {
        this.chatChannelView = chatChannelView;
        chatChannelService = new FirebaseChatChannelService();
        chatChannelService.setOnNewChatChannel(new Callback<ChatChannelInfo>(){
            @Override
            public void onSuccess(ChatChannelInfo result) {
                addNewChatChannel(result);
            }

            @Override
            public void onError(Throwable what) {
                chatChannelView.showErrorMessage(what.getMessage());
            }
        });
    }

    private void addNewChatChannel(ChatChannelInfo chatChannelInfo) {
        chatChannelView.addNewChatChannel(chatChannelInfo);
    }

    @Override
    public void setup(String userId) {
        chatChannelService.setup(userId);
    }

    @Override
    public void fetchAvailableChatChannelsAsync() {
        chatChannelService.fetchAvailableChatChannelsAsync();
    }
}
