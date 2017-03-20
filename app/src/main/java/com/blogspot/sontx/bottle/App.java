package com.blogspot.sontx.bottle;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;

public class App extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fresco.initialize(this);
        setupEnvironmentProperties();
    }

    private void setupEnvironmentProperties() {
        System.setProperty(Constants.FIREBASE_USER_PUBLIC_INFO_KEY, "users_public_info");
        System.setProperty(Constants.FIREBASE_CHAT_USER_CHANNEL_KEY, "user_channels");
        System.setProperty(Constants.FIREBASE_CHAT_CHANNEL_KEY, "channels");
        System.setProperty(Constants.FIREBASE_CHAT_CHANNEL_MESSAGES_KEY, "messages");
        System.setProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_KEY, "info");
        System.setProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_CREATE_KEY, "create");
        System.setProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_LAST_ACTIVE_KEY, "last_active");
        System.setProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_USER1_ID_KEY, "user1_id");
        System.setProperty(Constants.FIREBASE_CHAT_CHANNEL_INFO_USER2_ID_KEY, "user2_id");
        System.setProperty(Constants.UI_DEFAULT_AVATAR_URL_KEY, "https://scontent.fdad3-1.fna.fbcdn.net/v/t1.0-9/15327440_1025737524238650_8411420773439628497_n.jpg?oh=79f18a0efe45c460a152a8eb5885442c&oe=5968FA22");
        System.setProperty(Constants.UI_DEFAULT_DISPLAY_NAME_KEY, "sontx");

        System.setProperty(Constants.OPTIMIZE_IMAGE_MIN_WIDTH_KEY, "600");
        System.setProperty(Constants.OPTIMIZE_IMAGE_MIN_HEIGH_KEY, "600");
    }
}
