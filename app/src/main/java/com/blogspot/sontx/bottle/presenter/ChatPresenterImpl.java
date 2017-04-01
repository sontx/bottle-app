package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.presenter.interfaces.ChatPresenter;
import com.blogspot.sontx.bottle.system.event.ChatMessageReceivedEvent;
import com.blogspot.sontx.bottle.system.event.ChatTextMessageEvent;
import com.blogspot.sontx.bottle.system.event.RegisterServiceEvent;
import com.blogspot.sontx.bottle.system.event.ServiceState;
import com.blogspot.sontx.bottle.system.event.ServiceStateEvent;
import com.blogspot.sontx.bottle.system.service.MessagingService;
import com.blogspot.sontx.bottle.view.interfaces.ChatView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import it.slyce.messaging.message.Message;
import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;
import lombok.Setter;

public class ChatPresenterImpl extends PresenterBase implements ChatPresenter {
    private final ChatView chatView;
    private String currentUserId;
    @Setter
    private Channel channel;

    public ChatPresenterImpl(ChatView chatView, String currentUserId) {
        this.chatView = chatView;
        this.currentUserId = currentUserId;
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        if (MessagingService.isRunning())
            registerToService();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        unregisterToService();
    }

    @Override
    public void sendAsync(String text) {
        ChatTextMessageEvent chatTextMessageEvent = new ChatTextMessageEvent();
        chatTextMessageEvent.setId(0);
        chatTextMessageEvent.setChannelId(channel.getId());
        chatTextMessageEvent.setText(text);
        EventBus.getDefault().post(chatTextMessageEvent);
    }

    @Override
    public PublicProfile getCurrentPublicProfile() {
        if (channel != null) {
            List<ChannelMember> memberList = channel.getMemberList();
            if (memberList != null) {
                for (ChannelMember member : memberList) {
                    if (member.getId().equalsIgnoreCase(currentUserId))
                        return member.getPublicProfile();
                }
            }
        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessageReceivedEvent(ChatMessageReceivedEvent chatMessageReceivedEvent) {
        ChatMessage chatMessage = chatMessageReceivedEvent.getChatMessage();
        if (chatMessage.getChannelId().equalsIgnoreCase(channel.getId()))
            addNewMessage(chatMessage);
    }

    @Subscribe
    public void onServiceStateEvent(ServiceStateEvent serviceStateEvent) {
        if (serviceStateEvent.getServiceState() == ServiceState.RUNNING)
            registerToService();
    }

    private void registerToService() {
        RegisterServiceEvent registerServiceEvent = new RegisterServiceEvent();
        registerServiceEvent.setRegister(true);
        EventBus.getDefault().post(registerServiceEvent);
    }

    private void unregisterToService() {
        RegisterServiceEvent registerServiceEvent = new RegisterServiceEvent();
        registerServiceEvent.setRegister(false);
        EventBus.getDefault().post(registerServiceEvent);
    }

    private void addNewMessage(ChatMessage value) {
        if (value.getMessageType() == null)
            return;

        Message message = null;
        if (value.getMessageType().equalsIgnoreCase(ChatMessage.TYPE_TEXT)) {
            TextMessage textMessage = new TextMessage();
            textMessage.setText(value.getMessage());
            message = textMessage;
        }

        if (message != null) {
            message.setDate(value.getTimestamp());
            message.setSource(value.getSenderId().equalsIgnoreCase(currentUserId) ? MessageSource.LOCAL_USER : MessageSource.EXTERNAL_USER);
            message.setUserId(value.getSenderId());

            PublicProfile senderPublicProfile = getPublicProfileById(value.getSenderId());
            if (senderPublicProfile != null) {
                message.setDisplayName(senderPublicProfile.getDisplayName());
                message.setAvatarUrl(senderPublicProfile.getAvatarUrl());

                chatView.addNewMessage(message);
            }
        }
    }

    private PublicProfile getPublicProfileById(String senderId) {
        if (channel != null) {
            List<ChannelMember> memberList = channel.getMemberList();
            if (memberList != null) {
                for (ChannelMember member : memberList) {
                    if (member.getId().equalsIgnoreCase(senderId))
                        return member.getPublicProfile();
                }
            }
        }
        return null;
    }
}
