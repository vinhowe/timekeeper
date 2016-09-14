package com.base512.accountant.data;

import android.os.Parcelable;

import com.google.common.base.Optional;

public abstract class DataObject {
    private final Optional<String> mId;

    protected DataObject(String id) {
        this.mId = id.equals("") ? Optional.<String>absent() : Optional.of(id);
    }

    protected DataObject() {
        this.mId = Optional.absent();
    }

    public Optional<String> getId() {
        return mId;
    }
}
