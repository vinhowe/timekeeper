/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.data;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import java.util.LinkedHashMap;

public class Day extends DataObject {
    private final long mDate;
    private final DayTask mCurrentTask;
    private final LinkedHashMap<String, DayTask> mTasks;
    private final State mState;

    public Day(long date, @NonNull String id, DayTask currentTask, @NonNull LinkedHashMap<String, DayTask> tasks, @NonNull State state) {
        super(id);
        this.mDate = date;
        mCurrentTask = currentTask;
        this.mTasks = tasks;
        mState = state;
    }

    public Day(long date, @NonNull String id, @NonNull LinkedHashMap<String, DayTask> tasks) {
        super(id);
        this.mDate = date;
        mCurrentTask = null;
        this.mTasks = tasks;
        mState = State.NONE;
    }

    public Day(long date, @NonNull String id) {
        super(id);
        this.mDate = date;
        this.mTasks = new LinkedHashMap<>();
        mCurrentTask = null;
        mState = State.NONE;
    }

    public Day(long date, @NonNull String id, State state) {
        super(id);
        this.mDate = date;
        this.mTasks = new LinkedHashMap<>();
        mCurrentTask = null;
        mState = state;
    }

    public Day(long date) {
        super();
        this.mDate = date;
        this.mTasks = new LinkedHashMap<>();
        mCurrentTask = null;
        mState = State.NONE;
    }

    public long getDate() {
        return mDate;
    }

    public State getState() {
        return mState;
    }

    public Optional<DayTask> getCurrentTask() {
        return mCurrentTask == null ? Optional.<DayTask>absent() : Optional.of(mCurrentTask);
    }

    public LinkedHashMap<String, DayTask> getTasks() {
        return mTasks;
    }

    public Optional<Day> complete() {
        if(!getId().isPresent() || !getCurrentTask().isPresent()) {
            return Optional.absent();
        }
        if(mState.equals(State.COMPLETE)) {
            return Optional.of(this);
        }
        return Optional.of(new Day(getDate(), getId().get(), getCurrentTask().get(), mTasks, State.COMPLETE));
    }

    public Optional<Day> start() {
        if(!getId().isPresent() || !getCurrentTask().isPresent()) {
            return Optional.absent();
        }
        if(mState.equals(State.RUNNING)) {
            return Optional.of(this);
        }
        return Optional.of(new Day(getDate(), getId().get(), getCurrentTask().get(), mTasks, State.RUNNING));
    }

    public Day setCurrentTask(DayTask currentTask) {
        return new Day(mDate, getId().get(), currentTask, mTasks, mState);
    }

    public enum State {
        NONE, SCHEDULED, RUNNING, COMPLETE
    }
}
