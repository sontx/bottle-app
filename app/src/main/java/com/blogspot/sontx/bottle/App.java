package com.blogspot.sontx.bottle;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.blogspot.sontx.bottle.system.BottleContext;
import com.blogspot.sontx.bottle.system.BottleContextWrapper;
import com.google.firebase.database.FirebaseDatabase;

import lombok.Getter;

public class App extends MultiDexApplication {
    private static final String MAIN_SERVER_ADDRESS = "sontx.ddns.net:8080";
    private static final String FS_SERVER_ADDRESS = "sontx.ddns.net:8080";

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
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
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
        System.setProperty(Constants.FIREBASE_QRCODES_KEY, "qrcodes");

        System.setProperty(Constants.BOTTLE_SERVER_BASE_URL_KEY, "http://" + MAIN_SERVER_ADDRESS + "/bottle/rest/");
        System.setProperty(Constants.BOTTLE_SERVER_STOMP_ENDPOINT_KEY, "ws://" + MAIN_SERVER_ADDRESS + "/bottle/notify");
        System.setProperty(Constants.BOTTLE_FS_BASE_URL_KEY, "http://" + FS_SERVER_ADDRESS + "/bottlefs/rest/");
        System.setProperty(Constants.BOTTLE_FS_STORAGE_URL_KEY, "http://" + FS_SERVER_ADDRESS + "/bottlefs/resources");
    }
}
