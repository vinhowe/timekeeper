package com.base512.accountant.data;

import android.util.Log;

import com.base512.accountant.util.StringUtils;

import org.joda.time.DateTime;

import java.util.Arrays;

/**
 * Created by Thomas on 11/29/2016.
 */

public final class DayOfWeekSchedule extends Schedule {

    private final boolean[] mDays;

    public DayOfWeekSchedule(boolean[] days) {
        mDays = days;
    }

    @Override
    public boolean checkScheduleForDate(DateTime date) {
        return mDays[date.getDayOfWeek()-1];
    }

    public String getHumanSummary() {
        return StringUtils.makeFormattedWeekSchedule(mDays);
    }
}
