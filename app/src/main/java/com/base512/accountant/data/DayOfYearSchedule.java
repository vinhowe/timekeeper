package com.base512.accountant.data;

import org.joda.time.DateTime;

/**
 * Created by Thomas on 11/29/2016.
 */

public final class DayOfYearSchedule extends Schedule {

    final int[] mDaysOfYear;

    public DayOfYearSchedule(int[] daysOfYear) {
        mDaysOfYear = daysOfYear;
    }

    @Override
    public boolean checkScheduleForDate(DateTime date) {
        for(int dateNumber : mDaysOfYear) {
            if(dateNumber == date.getDayOfYear()) {
                return true;
            }
        }
        return false;
    }
}
