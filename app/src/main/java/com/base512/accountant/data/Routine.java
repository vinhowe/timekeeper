package com.base512.accountant.data;

import java.util.LinkedHashMap;

/**
 * Created by Thomas on 10/19/2016.
 */

public class Routine extends Task {

    private final LinkedHashMap<String, ConditionalTask> mTasks;

    public Routine(String label, String id, int estimatedDuration, LinkedHashMap<String, ConditionalTask> tasks) {
        super(label, id, estimatedDuration);
        mTasks = tasks;
    }

    public LinkedHashMap<String, ConditionalTask> getConditionalTasks() {
        return mTasks;
    }
}
