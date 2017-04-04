package it.slyce.messaging.utils;

import android.content.Context;
import android.text.format.DateUtils;

import it.slyce.messaging.R;

/**
 * Created by matthewpage on 6/27/16.
 */
public class DateTimeUtils {

    public static String getTimestamp(Context context, long then) {
        long now = System.currentTimeMillis();

        long nowSeconds = now / 1000;
        long thenSeconds = then / 1000;

        int minutesAgo = ((int) (nowSeconds - thenSeconds)) / (60);
        if (minutesAgo < 1)
            return context.getString(R.string.date_now);

        return DateUtils.getRelativeTimeSpanString(then, now, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    }

    public static boolean dateNeedsUpdated(Context context, long time, String date) {
        return date == null || date.equals(getTimestamp(context, time));
    }
}
