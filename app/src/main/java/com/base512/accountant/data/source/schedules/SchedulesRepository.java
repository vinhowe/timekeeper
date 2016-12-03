/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.data.source.schedules;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import android.support.annotation.NonNull;
import android.util.Log;

import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.DayOfWeekSchedule;
import com.base512.accountant.data.DynamicTask;
import com.base512.accountant.data.Schedule;
import com.base512.accountant.data.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SchedulesRepository implements SchedulesDataSource {

    private final DatabaseReference mDatabaseReference;
    private final String TAG = SchedulesRepository.class.getSimpleName();

    @Inject
    public SchedulesRepository(DatabaseReference databaseReference) {
        mDatabaseReference = databaseReference.child("schedules");
    }

    @Override
    public void getSchedules(@NonNull final LoadDataCallback<Schedule> callback) {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // @SuppressWarnings("unchecked")
                //LinkedHashMap<String, DataObject> schedules = (LinkedHashMap<String, DataObject>)(LinkedHashMap) getSchedulesFromSnapshot(dataSnapshot);
                // callback.onDataLoaded(schedules);
                // TODO Figure out how to get schedules
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataError();
            }
        });
    }

    @Override
    public void getSchedule(@NonNull String scheduleId, @NonNull final GetDataCallback<Schedule> callback) {
        mDatabaseReference.child(scheduleId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Schedule schedule;
                if(dataSnapshot.child("daysOfWeek").exists()) {
                    boolean[] daysOfWeek = new boolean[7];
                    for (DataSnapshot dayOfWeek : dataSnapshot.child("daysOfWeek").getChildren()) {
                        daysOfWeek[Integer.valueOf(dayOfWeek.getKey())] = dayOfWeek.getValue(Boolean.class);
                    }
                    schedule = new DayOfWeekSchedule(daysOfWeek);
                    callback.onDataLoaded(schedule);
                } else if (dataSnapshot.child("daysOfYear").exists()) {
                    // TODO Figure out how to handle "Day of Year" schedules
                } else {
                    Log.e(TAG, "Unsupported schedule type");
                    callback.onDataError();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataError();
            }
        });
    }

    @Override
    public void saveSchedule(@NonNull final Schedule schedule, final UpdateDataCallback callback) {
        final DatabaseReference newScheduleReference = schedule.getId().isPresent() ? mDatabaseReference.child(schedule.getId().get()) : mDatabaseReference.push();
        // TODO figure out how schedule saving works
    }

    @Override
    public void setScheduleLabel(@NonNull String scheduleId, @NonNull String label) {
        mDatabaseReference.child(scheduleId).child("label").setValue(label);
    }
}
