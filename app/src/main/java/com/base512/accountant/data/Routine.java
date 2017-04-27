package com.base512.accountant.data;

import android.support.annotation.Nullable;

import java.util.LinkedHashMap;

/**
 * Created by Thomas on 10/19/2016.
 */

public class Routine extends Task {

    private final LinkedHashMap<String, Task> mTasks;

    public Routine(String label, String id, int estimatedDuration, LinkedHashMap<String, Task> tasks, @Nullable Schedule schedule) {
        super(label, id, estimatedDuration, schedule);
        mTasks = tasks;
    }

    public LinkedHashMap<String, Task> getConditionalTasks() {
        return mTasks;
    }
}
