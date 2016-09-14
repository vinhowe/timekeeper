package com.base512.accountant.data;

public class DynamicTask extends Task {
    private final int projectedDuration;

    public DynamicTask(String label, String id, int expectedDuration, int projectedDuration) {
        super(label, id, expectedDuration);
        this.projectedDuration = projectedDuration;
    }

    public DynamicTask(String label, String id, int duration) {
        super(label, id, duration);
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
