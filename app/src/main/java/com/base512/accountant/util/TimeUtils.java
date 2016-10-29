package com.base512.accountant.util;

import com.squareup.phrase.Phrase;

import org.joda.time.LocalDate;

/**
 * Created by Thomas on 8/30/2016.
 */

public class TimeUtils {

    public static long getMidnightTime() {
        return new LocalDate().toDateTimeAtStartOfDay().toInstant().getMillis();
    }

    public static String formatSeconds(long seconds) {
        long displaySeconds = seconds;
        String secondsLabel = seconds == 1 ? "second" : "seconds";
        String timeFields = "";

        if(seconds >= 60) {
            displaySeconds = seconds % 60;
            secondsLabel = displaySeconds == 1 ? "second" : "seconds";
            timeFields = formatMinutes(seconds / 60);
        }
        return Phrase.from((timeFields.isEmpty() ? "" : "{time_fields} ")+"{seconds} {seconds_label}")
                .putOptional("time_fields", timeFields)
                .put("seconds", String.valueOf(displaySeconds))
                .put("seconds_label", secondsLabel).format().toString();
    }

    public static String formatMinutes(long minutes) {
        long displayMinutes = minutes;
        String minutesLabel = minutes == 1 ? "minute" : "minutes";
        String timeFields = "";

        if(minutes >= 60) {
            long hours = minutes / 60;
            String hoursLabel = hours == 1 ? "hour" : "hours";
            if(minutes % 60 == 0) {
                return hours + " " + hoursLabel;
            }
            displayMinutes = minutes % 60;
            minutesLabel = displayMinutes == 1 ? "minute" : "minutes";
            timeFields = Phrase.from("{hours} {hours_label}")
                    .put("hours", String.valueOf(hours))
                    .put("hours_label", hoursLabel)
                    .format()
                    .toString();
            if(hours >= 24) {
                long days = hours / 24;
                String daysLabel = days == 1 ? "day" : "days";
                if(hours % 24 == 0) {
                    return days + " " + daysLabel;
                }
                long displayDays = hours % 24;
                daysLabel = displayDays == 1 ? "day" : "days";
                timeFields = Phrase.from("{days} {days_label} {time_fields}")
                        .put("days", String.valueOf(days))
                        .put("days_label", daysLabel)
                        .put("time_fields", timeFields)
                        .format()
                        .toString();
            }
        }
        return Phrase.from((timeFields.isEmpty() ? "" : "{time_fields} ")+"{minutes} {minutes_label}")
                .putOptional("time_fields", timeFields)
                .put("minutes", String.valueOf(displayMinutes))
                .put("minutes_label", minutesLabel).format().toString();
    }
}
