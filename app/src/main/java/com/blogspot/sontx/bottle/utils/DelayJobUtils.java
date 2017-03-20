package com.blogspot.sontx.bottle.utils;

import android.os.Handler;
import android.os.Message;

public final class DelayJobUtils {
    private static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    public static void delay(Runnable runnable, int millis) {
        handler.postDelayed(runnable, millis);
    }

    private DelayJobUtils() {
    }
}
