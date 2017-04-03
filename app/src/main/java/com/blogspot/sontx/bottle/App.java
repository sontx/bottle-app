package com.blogspot.sontx.bottle;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class App extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupEnvironmentProperties();
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
    }
}
