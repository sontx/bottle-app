package com.blogspot.sontx.bottle.presenter;

import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.presenter.interfaces.ChatPresenter;
import com.blogspot.sontx.bottle.system.event.ChatMessageReceivedEvent;
import com.blogspot.sontx.bottle.system.event.RegisterServiceEvent;
import com.blogspot.sontx.bottle.system.event.RequestChatMessagesEvent;
import com.blogspot.sontx.bottle.system.event.ResponseChatMessagesEvent;
import com.blogspot.sontx.bottle.system.event.SendChatMessageResultEvent;
import com.blogspot.sontx.bottle.system.event.SendChatTextMessageEvent;
import com.blogspot.sontx.bottle.system.event.ServiceState;
import com.blogspot.sontx.bottle.system.event.ServiceStateChangedEvent;
import com.blogspot.sontx.bottle.system.service.MessagingService;
import com.blogspot.sontx.bottle.utils.DateTimeUtils;
import com.blogspot.sontx.bottle.view.interfaces.ChatView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import it.slyce.messaging.message.Message;
import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;
import lombok.Setter;

public class ChatPresenterImpl extends PresenterBase implements ChatPresenter {
    private static final int MAX_INITIAL_MESSAGES = 15;
    private static final int MAX_LOAD_MORE_MESSAGES = 10;

    private final ChatView chatView;
    private String currentUserId;
    private long oldestMessageTimestamp = 0;
    private boolean isFirstLoadChatMessagesHistory = false;
    @Setter
    private Channel channel;

    public ChatPresenterImpl(ChatView chatView, String currentUserId) {
        this.chatView = chatView;
        this.currentUserId = currentUserId;
    }

    @Override
    public void onStart() {
        registerEventBusIfNecessary();

        if (MessagingService.isRunning())
            registerToService();

        if (!isFirstLoadChatMessagesHistory) {
            isFirstLoadChatMessagesHistory = true;
            requestChatMessagesHistory(MAX_INITIAL_MESSAGES);
        }
    }

    private void requestChatMessagesHistory(int count) {
        long startAt = oldestMessageTimestamp <= 0 ? DateTimeUtils.utc() : oldestMessageTimestamp - 1;

        RequestChatMessagesEvent requestChatMessagesEvent = new RequestChatMessagesEvent();
        requestChatMessagesEvent.setCurrentUserId(currentUserId);
        requestChatMessagesEvent.setChannelId(channel.getId());
        requestChatMessagesEvent.setLimit(count);
        requestChatMessagesEvent.setStartAtTimestamp(startAt);
        EventBus.getDefault().post(requestChatMessagesEvent);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        unregisterToService();
    }

    @Override
    public void sendAsync(String text, int internalId) {
        SendChatTextMessageEvent sendChatTextMessageEvent = new SendChatTextMessageEvent();
        sendChatTextMessageEvent.setId(internalId);
        sendChatTextMessageEvent.setChannelId(channel.getId());
        sendChatTextMessageEvent.setText(text);
        EventBus.getDefault().post(sendChatTextMessageEvent);

        long now = DateTimeUtils.utc();
        if (oldestMessageTimestamp < now)
            oldestMessageTimestamp = now;
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

    @Override
    public void requestLoadMoreMessages() {
        requestChatMessagesHistory(MAX_LOAD_MORE_MESSAGES);
    }

    /**
     * --------------------------------- begin subscribe methods --------------------------------
     **/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendChatMessageResultEvent(SendChatMessageResultEvent sendChatMessageResultEvent) {
        ChatMessage result = sendChatMessageResultEvent.getResult();
        if (result != null)
            chatView.updateChatMessage(sendChatMessageResultEvent.getId(), result.getState());
        else
            chatView.updateChatMessage(sendChatMessageResultEvent.getId(), ChatMessage.STATE_ERROR);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewChatMessageReceivedEvent(ChatMessageReceivedEvent chatMessageReceivedEvent) {
        ChatMessage chatMessage = chatMessageReceivedEvent.getChatMessage();
        if (chatMessage.getChannelId().equalsIgnoreCase(channel.getId()) && !chatMessage.getSenderId().equalsIgnoreCase(currentUserId)) {
            addNewMessage(chatMessage);
            if (oldestMessageTimestamp < chatMessage.getTimestamp())
                oldestMessageTimestamp = chatMessage.getTimestamp();
        }
    }

    @Subscribe
    public void onServiceStateChangedEvent(ServiceStateChangedEvent serviceStateChangedEvent) {
        if (serviceStateChangedEvent.getServiceState() == ServiceState.RUNNING)
            registerToService();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onResponseChatMessagesEvent(ResponseChatMessagesEvent responseChatMessagesEvent) {
        if (responseChatMessagesEvent.getChannelId().equalsIgnoreCase(channel.getId())) {
            List<ChatMessage> chatMessageList = responseChatMessagesEvent.getChatMessageList();
            if (chatMessageList.isEmpty()) {

            } else {
                List<Message> messageList = new ArrayList<>(chatMessageList.size());
                for (ChatMessage chatMessage : chatMessageList) {
                    Message message = convertChatMessage(chatMessage);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                oldestMessageTimestamp = chatMessageList.get(0).getTimestamp();
                chatView.onHasMoreMessages(messageList);
            }
        }
    }

    /**
     * --------------------------------- end subscribe methods ----------------------------------
     **/

    private void registerEventBusIfNecessary() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
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
        Message message = convertChatMessage(value);
        if (message != null)
            chatView.addNewMessage(message);
    }

    private Message convertChatMessage(ChatMessage value) {
        if (value.getMessageType() == null)
            return null;

        Message message = null;
        if (value.getMessageType().equalsIgnoreCase(ChatMessage.TYPE_TEXT)) {
            TextMessage textMessage = new TextMessage();
            textMessage.setText(value.getMessage());
            message = textMessage;
        }

        if (message != null) {
            message.setState(value.getState());
            message.setDate(value.getTimestamp());
            message.setSource(value.getSenderId().equalsIgnoreCase(currentUserId) ? MessageSource.LOCAL_USER : MessageSource.EXTERNAL_USER);
            message.setUserId(value.getSenderId());

            PublicProfile senderPublicProfile = getPublicProfileById(value.getSenderId());
            if (senderPublicProfile != null) {
                message.setDisplayName(senderPublicProfile.getDisplayName());
                message.setAvatarUrl(senderPublicProfile.getAvatarUrl());

                return message;
            }
        }

        return null;
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
