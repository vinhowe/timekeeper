package com.base512.accountant.data;

import android.app.VoiceInteractor;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.google.common.base.Optional;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Comparator;

import static com.base512.accountant.data.DayTask.TaskState.COMPLETE;
import static com.base512.accountant.data.DayTask.TaskState.PAUSED;
import static com.base512.accountant.data.DayTask.TaskState.RUNNING;
import static com.base512.accountant.data.DayTask.TaskState.SKIPPED;

@IgnoreExtraProperties
public class DayTask extends DataObject implements Comparable<DayTask> {
    private final TaskState mState;
    private final int mOrder;
    private final long mLastStartTime;
    private final long mAccumulatedTime;

    public DayTask(@NonNull String id, TaskState state, int order, long lastStartTime, long accumulatedTime) {
        super(id);
        mState = state;
        mOrder = order;
        mLastStartTime = lastStartTime;
        mAccumulatedTime = accumulatedTime;
    }


    public DayTask(@NonNull String id, TaskState state) {
        super(id);
        mState = state;
        mOrder = 0;
        mLastStartTime = 0;
        mAccumulatedTime = 0;
    }

    public DayTask(Task task) throws NullPointerException {
        super(task.getId().get());
        if(!getId().isPresent()) {
            throw new NullPointerException();
        }
        mState = TaskState.NONE;
        mOrder = 0;
        mLastStartTime = 0;
        mAccumulatedTime = 0;
    }

    public TaskState getState() {
        return mState;
    }

    public Optional<Integer> getOrder() {
        return mOrder == -1 ? Optional.<Integer>absent() : Optional.of(mOrder);
    }

    @Override
    public int compareTo(DayTask o)  {
        if(!getOrder().isPresent() || !o.getOrder().isPresent()) {
            return 0;
        }
        return getOrder().get() > o.getOrder().get() ? 1 : -1;
    }

    public boolean isRunning() {
        return mState.equals(TaskState.RUNNING);
    }

    public long getAccumulatedTime() {
        return mAccumulatedTime;
    }

    public Optional<Long> getLastStartTime() {
        return mLastStartTime == 0 ? Optional.<Long>absent() : Optional.of(mLastStartTime);
    }

    public Optional<Long> getTotalTime() {
        if(!getLastStartTime().isPresent()) {
            return Optional.absent();
        }
        if (mState != RUNNING) {
            return Optional.of(mAccumulatedTime);
        }

        return Optional.of(mAccumulatedTime + (now() - mLastStartTime));
    }

    public DayTask start() {
        if(mState == RUNNING) {
            return this;
        }
        return new DayTask(getId().get(), TaskState.RUNNING, getOrder().get(), now(), getAccumulatedTime());
    }

    public DayTask pause() {
        if (mState != RUNNING) {
            return this;
        }

        final long accumulatedTime = mAccumulatedTime + (now() - mLastStartTime);
        return new DayTask(getId().get(), PAUSED, getOrder().get(), Long.MIN_VALUE, accumulatedTime);
    }

    public DayTask skip() {
        if (mState == SKIPPED) {
            return this;
        }

        final long accumulatedTime = mAccumulatedTime + (now() - mLastStartTime);
        return new DayTask(getId().get(), SKIPPED, getOrder().get(), Long.MIN_VALUE, accumulatedTime);
    }

    public DayTask complete() {
        if (mState == COMPLETE) {
            return this;
        }

        final long accumulatedTime = mAccumulatedTime + (now() - mLastStartTime);
        return new DayTask(getId().get(), COMPLETE, getOrder().get(), Long.MIN_VALUE, accumulatedTime);
    }

    public DayTask withOrder(int order) {
        if(this.getOrder().get().equals(order)) {
            return this;
        }
        return new DayTask(getId().isPresent() ? getId().get() : "", getState(), order, getLastStartTime().isPresent() ? getLastStartTime().get() : Long.MIN_VALUE, getAccumulatedTime());
    }

    public enum TaskState {
        NONE, RUNNING, COMPLETE, PAUSED, SKIPPED
    }

    private static long now() {
        return System.currentTimeMillis();
    }
}

