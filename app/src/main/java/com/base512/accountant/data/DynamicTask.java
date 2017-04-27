package com.base512.accountant.data;

import android.support.annotation.Nullable;

public class DynamicTask extends Task {

    private final int projectedDuration;

    public DynamicTask(String label, String id, int expectedDuration, int projectedDuration, @Nullable Schedule schedule) {
        super(label, id, expectedDuration, schedule);
        this.projectedDuration = projectedDuration;
    }

    public DynamicTask(String label, String id, int duration, @Nullable Schedule schedule) {
        super(label, id, duration, schedule);
        projectedDuration = 0;
    }

    public DynamicTask(String label, int duration) {
        super(label, duration);
        projectedDuration = 0;
    }

    public int getProjectedDuration() {
        return projectedDuration;
    }

}
