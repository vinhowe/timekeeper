package com.base512.accountant.data;

import android.os.Parcel;
import android.os.Parcelable;
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

    public boolean[] getSchedule() {
        return mDays;
    }

    public DayOfWeekSchedule(Parcel in){
        boolean[] days = new boolean[7];
        in.readBooleanArray(days);
        mDays = days;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBooleanArray(mDays);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public DayOfWeekSchedule createFromParcel(Parcel parcel) {
            return new DayOfWeekSchedule(parcel);
        }

        @Override
        public DayOfWeekSchedule[] newArray(int size) {
            return new DayOfWeekSchedule[size];
        }
    };
}
