package com.base512.accountant.data;

/**
 * Created by Thomas on 11/2/2016.
 */

import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Each task in a routine can have multiple schedules
 */
abstract public class Schedule extends DataObject implements Parcelable {

    public static final int DAYS_IN_A_WEEK = 7;

    public abstract boolean checkScheduleForDate(DateTime date);

}
