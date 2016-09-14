package com.base512.accountant.data;

import org.joda.time.LocalTime;

public class User extends DataObject {
    // Wake up time in total minutes from midnight
    private final int mWakeupTime;

    public User(int mWakeupTime) {
        this.mWakeupTime = mWakeupTime;
    }

    public int getWakeupTime() {
        return mWakeupTime;
    }
}
