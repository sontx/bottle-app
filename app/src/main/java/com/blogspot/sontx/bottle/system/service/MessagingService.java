package com.blogspot.sontx.bottle.system.service;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.system.event.ChangeCurrentUserEvent;
import com.blogspot.sontx.bottle.system.event.ChatMessageChangedEvent;
import com.blogspot.sontx.bottle.system.event.ChatMessageReceivedEvent;
import com.blogspot.sontx.bottle.system.event.RegisterServiceEvent;
import com.blogspot.sontx.bottle.system.event.RequestChatMessagesEvent;
import com.blogspot.sontx.bottle.system.event.ResponseChatMessagesEvent;
import com.blogspot.sontx.bottle.system.event.SendChatMessageResultEvent;
import com.blogspot.sontx.bottle.system.event.SendChatTextMessageEvent;
import com.blogspot.sontx.bottle.system.event.ServiceState;
import com.blogspot.sontx.bottle.system.event.ServiceStateChangedEvent;
import com.blogspot.sontx.bottle.system.event.UpdateChatMessageStateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.Getter;

public class MessagingService extends ServiceBase {
    private static final int NOTIFICATION_UNREAD_MESSAGE_ID = 0;
    @Getter
    private static boolean isRunning = false;
    private NotificationManager notificationManager;
    private ChatService chatService;
    private Queue<ChatMessage> messageQueue = new LinkedList<>();
    private boolean isRegister = false;

    @Override
    public void onCreate() {
        super.onCreate();

        chatService = FirebaseServicePool.getInstance().getChatService();
        chatService.setOnNewChatMessage(new IncommingNewChatMessageHandler());
        chatService.setOnChatMessageChanged(new IncommingChatMessageChangedHandler());

        EventBus.getDefault().register(this);

        isRunning = true;

        ServiceStateChangedEvent serviceStateChangedEvent = new ServiceStateChangedEvent();
        serviceStateChangedEvent.setServiceState(ServiceState.RUNNING);
        EventBus.getDefault().post(serviceStateChangedEvent);

        Log.d(TAG, "Service started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        if (chatService != null) {
            chatService.unregisterAllChannels();
            chatService.setOnNewChatMessage(null);
            chatService.setOnChatMessageChanged(null);
        }

        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_UNREAD_MESSAGE_ID);
        }

        isRunning = false;

        ServiceStateChangedEvent serviceStateChangedEvent = new ServiceStateChangedEvent();
        serviceStateChangedEvent.setServiceState(ServiceState.STOPPED);
        EventBus.getDefault().post(serviceStateChangedEvent);
    }

    /**
     * --------------------------------- begin subscribe methods --------------------------------
     **/

    @Subscribe
    public void onRegisterServiceEvent(RegisterServiceEvent registerServiceEvent) {
        isRegister = registerServiceEvent.isRegister();
    }

    @Subscribe
    public void onChangeCurrentUserEvent(ChangeCurrentUserEvent changeCurrentUserEvent) {

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUpdateChatMessageStateEvent(UpdateChatMessageStateEvent updateChatMessageStateEvent) {
        String currentUserId = App.getInstance().getBottleContext().getCurrentBottleUser().getUid();

        List<ChatMessage> chatMessageList = updateChatMessageStateEvent.getChatMessageList();
        String newState = updateChatMessageStateEvent.getNewState();
        for (ChatMessage chatMessage : chatMessageList) {
            if (!chatMessage.getSenderId().equals(currentUserId) && !chatMessage.getState().equalsIgnoreCase(newState)) {
                chatService.updateChatMessageStateAsync(chatMessage.getChannelId(), chatMessage.getId(), newState);
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSendChatTextMessageEvent(final SendChatTextMessageEvent sendChatTextMessageEvent) {
        chatService.sendAsync(sendChatTextMessageEvent.getChannelId(), sendChatTextMessageEvent.getText(), new Callback<ChatMessage>() {
            @Override
            public void onSuccess(ChatMessage result) {
                SendChatMessageResultEvent sendChatMessageResultEvent = new SendChatMessageResultEvent();
                sendChatMessageResultEvent.setChannelId(sendChatTextMessageEvent.getChannelId());
                sendChatMessageResultEvent.setId(sendChatTextMessageEvent.getId());
                sendChatMessageResultEvent.setResult(result);
                EventBus.getDefault().post(sendChatMessageResultEvent);
            }

            @Override
            public void onError(Throwable what) {
                SendChatMessageResultEvent sendChatMessageResultEvent = new SendChatMessageResultEvent();
                sendChatMessageResultEvent.setChannelId(sendChatTextMessageEvent.getChannelId());
                sendChatMessageResultEvent.setId(sendChatTextMessageEvent.getId());
                sendChatMessageResultEvent.setResult(null);
                EventBus.getDefault().post(sendChatMessageResultEvent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onRequestChatMessagesEvent(final RequestChatMessagesEvent requestChatMessagesEvent) {
        chatService.getMoreMessages(requestChatMessagesEvent.getChannelId(), requestChatMessagesEvent.getStartAtTimestamp(),
                requestChatMessagesEvent.getLimit(), new Callback<List<ChatMessage>>() {
                    @Override
                    public void onSuccess(List<ChatMessage> result) {
                        ResponseChatMessagesEvent responseChatMessagesEvent = new ResponseChatMessagesEvent();
                        responseChatMessagesEvent.setChannelId(requestChatMessagesEvent.getChannelId());
                        responseChatMessagesEvent.setChatMessageList(result);
                        EventBus.getDefault().post(responseChatMessagesEvent);
                    }

                    @Override
                    public void onError(Throwable what) {
                        Log.e(TAG, what.getMessage());
                    }
                });
    }

    /**
     * --------------------------------- end subscribe methods ----------------------------------
     **/

    private String getMessageDescription(ChatMessage chatMessage) {
        if (ChatMessage.TYPE_TEXT.equalsIgnoreCase(chatMessage.getMessageType()))
            return chatMessage.getMessage();
        else if (ChatMessage.TYPE_MEDIA.equalsIgnoreCase(chatMessage.getMessageType()))
            return "Media";
        return null;
    }

    private void showUnreadMessageNotification(ChatMessage chatMessage) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getMessageDescription(chatMessage))
                .setTicker("Unread message!");

        notificationManager.notify(NOTIFICATION_UNREAD_MESSAGE_ID, builder.build());
    }

    private class IncommingNewChatMessageHandler implements SimpleCallback<ChatMessage> {

        @Override
        public void onCallback(ChatMessage value) {

            if (isRegister) {
                ChatMessageReceivedEvent chatMessageReceivedEvent = new ChatMessageReceivedEvent();
                chatMessageReceivedEvent.setChatMessage(value);
                EventBus.getDefault().post(chatMessageReceivedEvent);
            } else {
                messageQueue.add(value);
                showUnreadMessageNotification(value);
            }
        }
    }

    private class IncommingChatMessageChangedHandler implements SimpleCallback<ChatMessage> {

        @Override
        public void onCallback(ChatMessage value) {
            if (isRegister) {
                ChatMessageChangedEvent chatMessageChangedEvent = new ChatMessageChangedEvent();
                chatMessageChangedEvent.setChatMessage(value);
                EventBus.getDefault().post(chatMessageChangedEvent);
            }
        }
    }
}
