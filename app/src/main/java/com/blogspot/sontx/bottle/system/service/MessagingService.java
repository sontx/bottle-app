package com.blogspot.sontx.bottle.system.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.model.bean.chat.ChatMessage;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatService;
import com.blogspot.sontx.bottle.system.event.ChangeCurrentUserEvent;
import com.blogspot.sontx.bottle.system.event.ChatChannelChangedEvent;
import com.blogspot.sontx.bottle.system.event.ChatMessageChangedEvent;
import com.blogspot.sontx.bottle.system.event.ChatMessageReceivedEvent;
import com.blogspot.sontx.bottle.system.event.NotifyRegisterMessageEvent;
import com.blogspot.sontx.bottle.system.event.OpenChannelEvent;
import com.blogspot.sontx.bottle.system.event.RegisterServiceEvent;
import com.blogspot.sontx.bottle.system.event.RequestChatMessagesEvent;
import com.blogspot.sontx.bottle.system.event.ResponseChatMessagesEvent;
import com.blogspot.sontx.bottle.system.event.SendChatMessageResultEvent;
import com.blogspot.sontx.bottle.system.event.SendChatTextMessageEvent;
import com.blogspot.sontx.bottle.system.event.ServiceState;
import com.blogspot.sontx.bottle.system.event.ServiceStateChangedEvent;
import com.blogspot.sontx.bottle.system.event.UpdateChatMessageStateEvent;
import com.blogspot.sontx.bottle.view.activity.ChatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import lombok.Getter;

public class MessagingService extends ServiceBase {
    private static final int NOTIFICATION_UNREAD_MESSAGE_ID = 0;
    @Getter
    private static boolean isRunning = false;
    private NotificationManager notificationManager;
    private ChatService chatService;
    private Queue<ChatMessage> messageQueue = new LinkedList<>();
    private boolean isRegister = false;

    private final Map<String, Integer> channelMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        chatService = FirebaseServicePool.getInstance().getChatService();
        if (chatService != null) {
            chatService.setOnNewChatMessage(new IncommingNewChatMessageHandler());
            chatService.setOnChatMessageChanged(new IncommingChatMessageChangedHandler());
        }

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
    public void onOpenChannelEvent(OpenChannelEvent openChannelEvent) {
        synchronized (channelMap) {
            Integer id = channelMap.get(openChannelEvent.getChannel().getId());
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(id);
        }
    }

    @Subscribe
    public void onNotifyRegisterMessageEvent(NotifyRegisterMessageEvent notifyRegisterMessageEvent) {
        if (chatService != null && !chatService.isRegisteredChatMessageListeners()) {
            chatService.setOnNewChatMessage(new IncommingNewChatMessageHandler());
            chatService.setOnChatMessageChanged(new IncommingChatMessageChangedHandler());
        }
    }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatChannelChangedEvent(ChatChannelChangedEvent chatChannelChangedEvent) {
        Channel channel = chatChannelChangedEvent.getChannel();
        if (channel == null)
            return;

        Channel currentChannel = ChatActivity.currentChannel;
        if (currentChannel != null && currentChannel.getId().equals(channel.getId()))
            return;

        ChannelMember anotherGuy = channel.getAnotherGuy();
        if (anotherGuy == null)
            return;
        PublicProfile publicProfile = anotherGuy.getPublicProfile();
        if (publicProfile == null)
            return;

/*
        String avatarUrl = publicProfile.getAvatarUrl();
        String url = App.getInstance().getBottleContext().getResource().absoluteUrl(avatarUrl);
*/

        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra(ChatActivity.CHANNEL_KEY, channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_logo)
                .setAutoCancel(true)
                .setContentTitle(publicProfile.getDisplayName())
                .setContentText(channel.getDetail().getLastMessage());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Integer id;
        synchronized (channelMap) {
            id = channelMap.get(channel.getId());
            if (id == null) {
                Random random = new Random(System.currentTimeMillis());
                id = random.nextInt();
                channelMap.put(channel.getId(), id);
            }
        }
        notificationManager.notify(id, builder.build());
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
