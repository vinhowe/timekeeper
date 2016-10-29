/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */

package com.base512.accountant.data.source.days;

import android.support.annotation.NonNull;

import com.base512.accountant.data.Day;
import com.base512.accountant.data.DayTask;
import com.base512.accountant.data.Task;
import com.base512.accountant.data.source.AccessDataCallback;
import com.base512.accountant.data.source.BaseDataSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public interface DaysDataSource extends BaseDataSource {

    void getDays(@NonNull LoadDataCallback callback);

    void getDay(@NonNull String dayId, @NonNull GetDataCallback callback);

    void getToday(@NonNull GetDataCallback callback);

    void getDayByDate(long instant, @NonNull GetDataCallback callback);

    void saveDay(@NonNull Day day, UpdateDataCallback callback);

    void updateDayTasks(@NonNull Day day, @NonNull ArrayList<DayTask> beforeTasks, @NonNull LinkedHashMap<String, DayTask> afterTasks, UpdateDataCallback callback);

    void addDayTask(@NonNull String dayId, @NonNull DayTask task);

    void addDayTask(@NonNull Day day, @NonNull DayTask task);

    void getDayTask(@NonNull Day day, @NonNull String taskId);

    void getDayTask(@NonNull String dayId, @NonNull String taskId);

    void removeDayTask(@NonNull Day day, @NonNull String taskId);

    void removeDayTask(@NonNull Day day, @NonNull DayTask task);

    void removeDayTask(@NonNull String dayId, @NonNull String taskId);

    void removeDayTask(@NonNull String dayId, @NonNull DayTask task);

    void completeTaskInDay(@NonNull Day day, @NonNull DayTask task);

    void completeTaskInDay(@NonNull Day day, @NonNull String taskId);

    void completeTaskInDay(@NonNull String dayId, @NonNull DayTask task);

    void completeTaskInDay(@NonNull String dayId, @NonNull String taskId);

    void setDayCurrentTask(@NonNull String dayId, @NonNull String taskId);

    void setDayCurrentTask(@NonNull String dayId, @NonNull DayTask dayTask);

    void setDayCurrentTask(@NonNull Day day, @NonNull String taskId);

    void setDayCurrentTask(@NonNull Day day, @NonNull DayTask dayTask);

    void setDayState(String dayId, Day.State dayState);
}
