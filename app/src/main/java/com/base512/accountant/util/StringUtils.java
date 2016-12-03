package com.base512.accountant.util;

import android.support.annotation.NonNull;

import com.squareup.phrase.ListPhrase;
import com.squareup.phrase.Phrase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Thomas on 12/2/2016.
 */

public class StringUtils {

    public static String makeFormattedWeekSchedule(boolean[] weekSchedule) {
        ArrayList<String> dayNames = new ArrayList<>();

        for(int i = 0; i < weekSchedule.length; i++) {
            if(weekSchedule[i]) {
                DateTime weekDayDate = new DateTime().withDayOfWeek(i+1);
                dayNames.add(weekDayDate.property(DateTimeFieldType.dayOfWeek()).getAsText()+"s");
            }
        }

        if(dayNames.size() == 7) {
            return "all week";
        }

        return makeFormattedList(dayNames, false);
    }

    public static String makeFormattedList(@NonNull Iterable<String> items, boolean oxfordComma) {
        ListPhrase listFormatter = ListPhrase.from(" and ", ", ", (oxfordComma ? ", and " : " and "));
        return listFormatter.join(items).toString();
    }
}
