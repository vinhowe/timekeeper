package com.base512.accountant.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Task extends DataObject implements Parcelable {
    private final String mLabel;
    private final int mDuration;

    public Task(String label, String id, int duration) {
        super(id);
        this.mLabel = label;
        this.mDuration = duration;
    }

    public Task(String label, int duration) {
        this.mLabel = label;
        this.mDuration = duration;
    }

    public String getLabel() {
        return mLabel;
    }

    public int getDuration() {
        return mDuration;
    }

    // Parcelling part
    public Task(Parcel in){
        super(in.readString());

        mLabel = in.readString();
        mDuration = in.readInt();
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
