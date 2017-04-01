package com.blogspot.sontx.bottle.system.service;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.system.event.ChatMessageReceivedEvent;
import com.blogspot.sontx.bottle.system.event.ChatMessageResponseEvent;
import com.blogspot.sontx.bottle.system.event.ChatTextMessageEvent;
import com.blogspot.sontx.bottle.system.event.RegisterServiceEvent;
import com.blogspot.sontx.bottle.system.event.ServiceState;
import com.blogspot.sontx.bottle.system.event.ServiceStateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
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
        chatService.setOnNewChatMessage(new IncommingChatMessageHandler());
        chatService.setOnChatMessageChanged(new IncommingChatMessageChangedHandler());

        EventBus.getDefault().register(this);

        isRunning = true;

        ServiceStateEvent serviceStateEvent = new ServiceStateEvent();
        serviceStateEvent.setServiceState(ServiceState.RUNNING);
        EventBus.getDefault().post(serviceStateEvent);

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
    }

    @Subscribe
    public void onRegisterServiceEvent(RegisterServiceEvent registerServiceEvent) {
        isRegister = registerServiceEvent.isRegister();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onChatTextMessageEvent(final ChatTextMessageEvent chatTextMessageEvent) {
        chatService.sendAsync(chatTextMessageEvent.getChannelId(), chatTextMessageEvent.getText(), new Callback<ChatMessage>() {
            @Override
            public void onSuccess(ChatMessage result) {
                ChatMessageResponseEvent chatMessageResponseEvent = new ChatMessageResponseEvent();
                chatMessageResponseEvent.setChannelId(chatTextMessageEvent.getChannelId());
                chatMessageResponseEvent.setId(chatTextMessageEvent.getId());
                chatMessageResponseEvent.setResult("Sent");
                EventBus.getDefault().post(chatMessageResponseEvent);
            }

            @Override
            public void onError(Throwable what) {
                ChatMessageResponseEvent chatMessageResponseEvent = new ChatMessageResponseEvent();
                chatMessageResponseEvent.setChannelId(chatTextMessageEvent.getChannelId());
                chatMessageResponseEvent.setId(chatTextMessageEvent.getId());
                chatMessageResponseEvent.setResult("Error");
                EventBus.getDefault().post(chatMessageResponseEvent);
            }
        });
    }

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

    private class IncommingChatMessageHandler implements SimpleCallback<ChatMessage> {

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

        }
    }
}
