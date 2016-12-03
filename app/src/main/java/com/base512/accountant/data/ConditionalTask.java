package com.base512.accountant.data;

import com.google.common.base.Optional;

/**
 * Created by Thomas on 11/29/2016.
 */

public final class ConditionalTask {
    private final Task mTask;
    private final Schedule mSchedule;

    public ConditionalTask(Task task) {
        mTask = task;
        mSchedule = null;
    }

    public ConditionalTask(Task task, Schedule schedule) {
        mTask = task;
        mSchedule = schedule;
    }

    public Task getTask() {
        return mTask;
    }

    public Optional<Schedule> getSchedule() {
        return Optional.fromNullable(mSchedule);
    }
}
