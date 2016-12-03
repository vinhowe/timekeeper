package com.base512.accountant.data;

/**
 * Created by Thomas on 11/2/2016.
 */

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Each task in a routine can have multiple schedules
 */
abstract public class Schedule extends DataObject {

    public abstract boolean checkScheduleForDate(DateTime date);

}
