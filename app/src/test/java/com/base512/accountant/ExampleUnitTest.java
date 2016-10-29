package com.base512.accountant;

import com.base512.accountant.data.DayTask;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedHashMap;

import static com.base512.accountant.util.TimeUtils.formatMinutes;
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
    }

/*    @Test
    public void formatTime() {
        for(int i = 0; i < 1500; i++) {
            //System.out.println(formatMinutes(i, true));
        }
    }*/
}