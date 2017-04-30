package com.blogspot.sontx.bottle;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.blogspot.sontx.bottle.model.service.FirebaseServicePool;
import com.blogspot.sontx.bottle.system.BottleContext;
import com.blogspot.sontx.bottle.system.BottleContextWrapper;

import lombok.Getter;

public class App extends MultiDexApplication {
    @Getter
    private static App instance;

    @Getter
    private BottleContextWrapper bottleContextWrapper;
    public App() {
        instance = this;
    }

    public BottleContext getBottleContext() {
        return bottleContextWrapper.getBottleContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupEnvironmentProperties();
        FirebaseServicePool.getInstance().initialize(this);
        bottleContextWrapper = new BottleContextWrapper();
    }

    private void setupEnvironmentProperties() {
        System.setProperty(Constants.UI_DEFAULT_AVATAR_URL_KEY, "https://scontent.fdad3-1.fna.fbcdn.net/v/t1.0-9/15327440_1025737524238650_8411420773439628497_n.jpg?oh=79f18a0efe45c460a152a8eb5885442c&oe=5968FA22");
        System.setProperty(Constants.UI_DEFAULT_DISPLAY_NAME_KEY, "sontx");

        System.setProperty(Constants.OPTIMIZE_IMAGE_MIN_WIDTH_KEY, "600");
        System.setProperty(Constants.OPTIMIZE_IMAGE_MIN_HEIGHT_KEY, "600");

        System.setProperty(Constants.FIREBASE_PUBLIC_PROFILE_KEY, "public_profiles");
        System.setProperty(Constants.FIREBASE_USER_CHANNEL_KEY, "user_channels");
        System.setProperty(Constants.FIREBASE_CHANNEL_DETAIL_KEY, "channel_details");
        System.setProperty(Constants.FIREBASE_CHANNEL_MEMBER_KEY, "channel_members");
        System.setProperty(Constants.FIREBASE_MESSAGES_KEY, "messages");

        System.setProperty(Constants.BOTTLE_SERVER_BASE_URL_KEY, "http://192.168.1.33:8080/bottle/rest/");
        System.setProperty(Constants.BOTTLE_FS_BASE_URL_KEY, "http://192.168.1.33:8080/bottlefs/rest/");
        System.setProperty(Constants.BOTTLE_FS_STORAGE_URL_KEY, "http://192.168.1.33:8080/bottlefs/res");
    }
}
