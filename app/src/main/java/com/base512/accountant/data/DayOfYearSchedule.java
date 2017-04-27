package com.base512.accountant.data;

import android.os.Parcel;
import android.os.Parcelable;

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

    public DayOfYearSchedule(Parcel in){
        int[] daysOfYear = new int[]{};
        in.readIntArray(daysOfYear);
        mDaysOfYear = daysOfYear;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(mDaysOfYear);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public DayOfYearSchedule createFromParcel(Parcel parcel) {
            return new DayOfYearSchedule(parcel);
        }

        @Override
        public DayOfYearSchedule[] newArray(int size) {
            return new DayOfYearSchedule[size];
        }
    };
}
