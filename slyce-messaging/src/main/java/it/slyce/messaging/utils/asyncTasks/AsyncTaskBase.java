package it.slyce.messaging.utils.asyncTasks;

import android.os.AsyncTask;

public abstract class AsyncTaskBase<P, T, R> extends AsyncTask<P, T, R> {
    private static final Object lock = new Object();

    public static Object getLock() {
        return lock;
    }
}
