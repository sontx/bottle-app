package it.slyce.messaging.utils.asyncTasks;

import android.os.AsyncTask;

public abstract class AsyncTaskBase extends AsyncTask {
    private static final Object lock = new Object();

    public static Object getLock() {
        return lock;
    }
}
