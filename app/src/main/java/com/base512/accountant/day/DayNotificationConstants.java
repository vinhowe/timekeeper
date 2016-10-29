package com.base512.accountant.day;

/**
 * Created by Thomas on 10/27/2016.
 */

public class DayNotificationConstants {
    public static final String ACTION_PREFIX = "com.base512.accountant.action.";

    public static final String ACTION_START_TASK = ACTION_PREFIX + "START_TASK";
    public static final String ACTION_PAUSE_TASK = ACTION_PREFIX + "PAUSE_TASK";
    public static final String ACTION_SKIP_TASK = ACTION_PREFIX + "SKIP_TASK";
    public static final String ACTION_COMPLETE_TASK = ACTION_PREFIX + "SKIP_TASK";

    public static final int DAY_NOTIFICATION_ID = Integer.MAX_VALUE - 1;
}
