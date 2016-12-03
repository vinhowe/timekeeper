package com.base512.accountant;

import android.support.annotation.NonNull;

import com.base512.accountant.data.DayOfWeekSchedule;
import com.base512.accountant.data.DayOfYearSchedule;
import com.base512.accountant.data.DayTask;
import com.squareup.phrase.ListPhrase;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static com.base512.accountant.util.TimeUtils.formatMinutes;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Test
    public void doesTodayWork() throws Exception {
        DateTime dateTime = new DateTime().withHourOfDay(8).withMinuteOfHour(0);
        DateTime plus30 = dateTime.plusMinutes(30);

        System.out.println(new LocalTime().withHourOfDay(14).withMinuteOfHour(0).toString("H:mm"));
    }

    @Test
    public void compareDayTasks() {
/*        DayTask dayTask1 = new DayTask("dayTask1", DayTask.TaskState.NONE, 0, lastStartTime, mAccumulatedTime, );
        DayTask dayTask2 = new DayTask("dayTask2", DayTask.TaskState.NONE, 1, lastStartTime, mAccumulatedTime, );
        LinkedHashMap<String, DayTask> dayTaskHashMap = new LinkedHashMap<>();
        System.out.println(dayTask1.compareTo(dayTask2));*/

        DayOfWeekSchedule schedule = new DayOfWeekSchedule(new boolean[]{false,true,false,false,false,false,true});

        assertEquals(true, schedule.checkScheduleForDate(new DateTime()));
    }

    @Test
    public void dayOfYearSchedule() {
        DayOfYearSchedule schedule = new DayOfYearSchedule(new int[]{1,2, 334});

        assertEquals(true, schedule.checkScheduleForDate(new DateTime()));
    }

    @Test
    public void makeFormattedWeekSchedule() {
        boolean[] weekSchedule = new boolean[]{true, true, false, true, true, true, true};
        ArrayList<String> dayNames = new ArrayList<>();

        for(int i = 0; i < weekSchedule.length; i++) {
            if(weekSchedule[i]) {
                DateTime weekDayDate = new DateTime().withDayOfWeek(i+1);
                dayNames.add(weekDayDate.property(DateTimeFieldType.dayOfWeek()).getAsText());
            }
        }

        System.out.println(makeOxfordFormattedList(dayNames));
    }

    public static String makeOxfordFormattedList(@NonNull Iterable<String> items) {
        ListPhrase listFormatter = ListPhrase.from(" and ", ", ", ", and ");
        return listFormatter.join(items).toString();
    }

/*    @Test
    public void formatTime() {
        for(int i = 0; i < 1500; i++) {
            //System.out.println(formatMinutes(i, true));
        }
    }*/
}