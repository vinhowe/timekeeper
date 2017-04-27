package com.base512.accountant.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class Task extends DataObject implements Parcelable {
    private final String mLabel;
    private final int mDuration;
    private final Schedule mSchedule;

    public Task(String label, String id, int duration, @Nullable Schedule schedule) {
        super(id);
        mLabel = label;
        mDuration = duration;
        mSchedule = schedule;
    }

    public Task(String label, int duration) {
        mLabel = label;
        mDuration = duration;
        mSchedule = null;
    }

    public Task(Task task, Schedule schedule) {
        super(task.getId().get());
        mLabel = task.getLabel();
        mDuration = task.getDuration();
        mSchedule = schedule;
    }

    public String getLabel() {
        return mLabel;
    }

    public int getDuration() {
        return mDuration;
    }

    public Schedule getSchedule() { return mSchedule; }

    // Parcelling part
    public Task(Parcel in){
        super(in.readString());

        mLabel = in.readString();
        mDuration = in.readInt();
        mSchedule = in.readParcelable(Schedule.class.getClassLoader());
    }

    @Override
    public int describeContents(){
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId().isPresent() ? getId().get() : "");
        dest.writeString(mLabel);
        dest.writeInt(mDuration);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
