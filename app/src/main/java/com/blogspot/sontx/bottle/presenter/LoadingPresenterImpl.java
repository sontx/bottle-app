package com.blogspot.sontx.bottle.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.model.service.Callback;
import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerAuthService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerMessageService;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleServerUserSettingService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChannelService;
import com.blogspot.sontx.bottle.model.service.interfaces.ChatServerLoginService;
import com.blogspot.sontx.bottle.model.service.interfaces.PublicProfileService;
import com.blogspot.sontx.bottle.presenter.interfaces.LoadingPresenter;
import com.blogspot.sontx.bottle.system.event.ChangeCurrentUserEvent;
import com.blogspot.sontx.bottle.system.event.ServiceState;
import com.blogspot.sontx.bottle.system.event.ServiceStateChangedEvent;
import com.blogspot.sontx.bottle.system.service.MessagingService;
import com.blogspot.sontx.bottle.view.custom.SupportVozEmojiProvider;
import com.blogspot.sontx.bottle.view.interfaces.LoadingView;
import com.vanniktech.emoji.EmojiManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * [Login to firebase server] -> fetch user access token from firebase server -> login to bottle server
 * -> fetch user setting from bottle server[init context] -> cache current user public profile
 * -> cache chat channels -> cache messages -> prepare UI
 */
public class LoadingPresenterImpl extends PresenterBase implements LoadingPresenter {
    private final LoadingView loadingView;

    public LoadingPresenterImpl(LoadingView loadingView) {
        this.loadingView = loadingView;
    }

    @Override
    public void registerListener() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void unregisterListener() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void loadIfNecessaryAsync() {
        onTaskCompleted(Task.ENTRY_POINT, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceStateChangedEvent(ServiceStateChangedEvent serviceStateChangedEvent) {
        if (serviceStateChangedEvent.getServiceState() == ServiceState.RUNNING)
            completePreparing();
    }

    private void completePreparing() {
        Log.d(TAG, "loading completed!");
        BottleUser currentBottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();
        notifyChangeCurrentUser(currentBottleUser.getUid());
        loadingView.onLoadSuccess();
    }

    private void notifyChangeCurrentUser(String newUserId) {
        ChangeCurrentUserEvent changeCurrentUserEvent = new ChangeCurrentUserEvent();
        changeCurrentUserEvent.setNewCurrentUserId(newUserId);
        EventBus.getDefault().post(changeCurrentUserEvent);
    }

    private void onTaskCompleted(Task task, Object result) {
        switch (task) {

            case ENTRY_POINT:// fetch firebase user token
                Log.d(TAG, "fetch firebase user token");
                ChatServerLoginService chatServerLoginService = FirebaseServicePool.getInstance().getChatServerLoginService();
                chatServerLoginService.getCurrentUserTokenAsync(new FetchFirebaseUserTokenTask());
                break;

            case FETCH_USER_TOKEN:// use firebase user token to login to bottle server
                Log.d(TAG, "use firebase user token to login to bottle server");
                BottleServerAuthService bottleServerAuthService = FirebaseServicePool.getInstance().getBottleServerAuthService();
                bottleServerAuthService.loginWithTokenAsync((String) result, new LoginBottleServerTask());
                break;

            case LOGIN_BOTTLE_SERVER:// fetch user setting from bottle server
                updateBottleUser();

                Log.d(TAG, "fetch user setting from bottle server");
                BottleServerUserSettingService bottleServerUserSettingService = FirebaseServicePool.getInstance().getBottleServerUserSettingService();
                bottleServerUserSettingService.getUserSettingAsync(new CacheUserSettingTask());
                break;

            case CACHE_USER_SETTING:// cache current user public profile
                updateUserSetting();

                Log.d(TAG, "cache current user public profile");
                bottleServerAuthService = FirebaseServicePool.getInstance().getBottleServerAuthService();
                String currentUserId = bottleServerAuthService.getCurrentBottleUser().getUid();
                PublicProfileService publicProfileService = FirebaseServicePool.getInstance().getPublicProfileService();
                publicProfileService.getPublicProfileAsync(currentUserId, new CachePublicProfileTask());
                break;

            case CACHE_PUBLIC_PROFILE:// cache list channel
                updateCurrentPublicProfile((PublicProfile) result);

                Log.d(TAG, "cache list channel");
                ChannelService channelService = FirebaseServicePool.getInstance().getChannelService();
                channelService.registerChannelEvents();

                onTaskCompleted(Task.CACHE_LIST_CHANNEL, null);
                break;

            case CACHE_LIST_CHANNEL:// fetch room/geo messages
                Log.d(TAG, "fetch room/geo messages");
                BottleServerMessageService bottleServerMessageService = FirebaseServicePool.getInstance().getBottleServerMessageService();
                bottleServerMessageService.cacheMessagesAsync(new CacheMessagesTask());
                break;

            case CACHE_MESSAGES:// prepare UI such as load emoji
                Log.d(TAG, "prepare UI");
                new PrepareUITask().execute();
                break;

            case PREPARE_UI:
                if (loadingView.isServiceRunning(MessagingService.class)) {
                    completePreparing();
                } else {
                    Log.d(TAG, "one more step, start messaging service...");
                    loadingView.getContext().startService(new Intent(loadingView.getContext(), MessagingService.class));
                }
                break;
        }
    }

    private void updateCurrentPublicProfile(PublicProfile publicProfile) {
        App.getInstance().getBottleContextWrapper().setCurrentPublicProfile(publicProfile);
    }

    private void updateUserSetting() {
        BottleServerUserSettingService bottleServerUserSettingService = FirebaseServicePool.getInstance().getBottleServerUserSettingService();
        UserSetting currentUserSetting = bottleServerUserSettingService.getCurrentUserSetting();
        App.getInstance().getBottleContextWrapper().setCurrentUserSetting(currentUserSetting);
    }

    private void updateBottleUser() {
        BottleServerAuthService bottleServerAuthService = FirebaseServicePool.getInstance().getBottleServerAuthService();
        BottleUser currentBottleUser = bottleServerAuthService.getCurrentBottleUser();
        App.getInstance().getBottleContextWrapper().setCurrentBottleUser(currentBottleUser);
    }

    private enum Task {
        ENTRY_POINT,
        FETCH_USER_TOKEN,
        LOGIN_BOTTLE_SERVER,
        CACHE_USER_SETTING,
        CACHE_PUBLIC_PROFILE,
        CACHE_LIST_CHANNEL,
        CACHE_MESSAGES,
        PREPARE_UI
    }

    private class FetchFirebaseUserTokenTask extends TaskBase<String> implements Callback<String> {

        @Override
        public void onSuccess(String result) {
            onTaskCompleted(Task.FETCH_USER_TOKEN, result);
        }
    }

    private class LoginBottleServerTask extends TaskBase<BottleUser> implements Callback<BottleUser> {

        @Override
        public void onSuccess(BottleUser result) {
            onTaskCompleted(Task.LOGIN_BOTTLE_SERVER, result);
        }
    }

    private class CacheUserSettingTask extends TaskBase<UserSetting> implements Callback<UserSetting> {

        @Override
        public void onSuccess(UserSetting result) {
            onTaskCompleted(Task.CACHE_USER_SETTING, result);
        }

    }

    private class CachePublicProfileTask extends TaskBase<PublicProfile> implements Callback<PublicProfile> {

        @Override
        public void onSuccess(PublicProfile result) {
            onTaskCompleted(Task.CACHE_PUBLIC_PROFILE, result);
        }
    }

    private class CacheMessagesTask extends TaskBase<Void> implements Callback<Void> {

        @Override
        public void onSuccess(Void result) {
            onTaskCompleted(Task.CACHE_MESSAGES, result);
        }
    }

    private class PrepareUITask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            EmojiManager.install(new SupportVozEmojiProvider());
            onTaskCompleted(Task.PREPARE_UI, null);
            return null;
        }
    }

    private abstract class TaskBase<T> implements Callback<T> {
        @Override
        public void onError(Throwable what) {
            loadingView.navigateToErrorActivity(what.getMessage());
            Log.e(TAG, what.getMessage());
        }
    }
}
