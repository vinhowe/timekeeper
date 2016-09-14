/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.data;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Day extends DataObject {
    private final long mDate;
    private final DayTask mCurrentTask;
    private final LinkedHashMap<String, DayTask> mTasks;

    public Day(long date, @NonNull String id, DayTask currentTask, @NonNull LinkedHashMap<String, DayTask> tasks) {
        super(id);
        this.mDate = date;
        mCurrentTask = currentTask;
        this.mTasks = tasks;
    }

    public Day(long date, @NonNull String id, @NonNull LinkedHashMap<String, DayTask> tasks) {
        super(id);
        this.mDate = date;
        mCurrentTask = null;
        this.mTasks = tasks;
    }

    public Day(long date, @NonNull String id) {
        super(id);
        this.mDate = date;
        this.mTasks = new LinkedHashMap<>();
        mCurrentTask = null;
    }

    public Day(long date) {
        super();
        this.mDate = date;
        this.mTasks = new LinkedHashMap<>();
        mCurrentTask = null;
    }

    public long getDate() {
        return mDate;
    }

    public Optional<DayTask> getCurrentTask() {
        return mCurrentTask == null ? Optional.<DayTask>absent() : Optional.of(mCurrentTask);
    }

    public LinkedHashMap<String, DayTask> getTasks() {
        return mTasks;
    }

    public Day setCurrentTask(DayTask currentTask) {
        return new Day(mDate, getId().get(), currentTask, mTasks);
    }
}
