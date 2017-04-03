package com.blogspot.sontx.bottle.utils;

import lombok.NonNull;

public final class ThreadUtils {

    private ThreadUtils() {
    }

    public static Thread run(@NonNull Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
}
