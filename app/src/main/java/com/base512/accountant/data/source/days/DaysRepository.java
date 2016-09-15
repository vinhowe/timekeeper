/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.data.source.days;

import android.support.annotation.NonNull;

import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.Day;
import com.base512.accountant.data.DayTask;
import com.base512.accountant.data.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
@Singleton
public class DaysRepository implements DaysDataSource {

    private final DatabaseReference mDatabaseReference;

    @Inject
    public DaysRepository(DatabaseReference databaseReference) {
        mDatabaseReference = databaseReference.child("days");
    }

    @Override
    public void getDays(@NonNull final LoadDataCallback callback) {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, DataObject> days = (LinkedHashMap<String, DataObject>)(LinkedHashMap)createDaysFromSnapshot(dataSnapshot);
                callback.onDataLoaded(days);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataError();
            }
        });
    }

    @Override
    public void getDay(@NonNull String dayId, @NonNull final GetDataCallback callback) {
        mDatabaseReference.child(dayId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Day day = createDayFromSnapshot(dataSnapshot);
                callback.onDataLoaded(day);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataError();
            }
        });
    }

    @Override
    public void getDayByDate(long instant, @NonNull final GetDataCallback callback) {
        mDatabaseReference.orderByChild("date").equalTo(instant).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the first and only child
                if(dataSnapshot.hasChildren()) {
                    DataSnapshot daySnapshot = dataSnapshot.getChildren().iterator().next();
                    if (!daySnapshot.exists()) {
                        callback.onDataError();
                    } else {
                        Day day = createDayFromSnapshot(daySnapshot);
                        callback.onDataLoaded(day);
                    }
                } else {
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
    public void saveDay(@NonNull final Day day, @NonNull final UpdateDataCallback callback) {
        DatabaseReference newDayReference = day.getId().isPresent() ? mDatabaseReference.child(day.getId().get()) : mDatabaseReference.push();
        newDayReference.child("date").setValue(day.getDate());
        final DatabaseReference tasksReference = newDayReference.child("tasks");
        tasksReference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                tasksReference.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        int i = 0;
                        for(DayTask dayTask : day.getTasks().values()) {
                            mutableData.child(dayTask.getId().get()).child("state").setValue(dayTask.getState().ordinal());
                            mutableData.child(dayTask.getId().get()).child("order").setValue(i);
                            i++;
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
            }
        });
    }

    @Override
    public void addDayTask(@NonNull String dayId, @NonNull final DayTask dayTask) {
        checkNotNull(dayId, "Day ID cannot be null");
        checkArgument(dayId.length() > 0, "Day ID cannot be empty");
        checkNotNull(dayTask, "Task cannot be null");

        final DatabaseReference dayTasksReference = mDatabaseReference.child(dayId).child("tasks");

        DatabaseReference dayTaskReference = dayTasksReference.child(dayTask.getId().get());

        dayTasksReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                MutableData dayTaskData = mutableData.child(dayTask.getId().get());
                dayTaskData.child("state").setValue(dayTask.getState().ordinal());
                dayTaskData.child("order").setValue(mutableData.getChildrenCount()-1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    @Override
    public void addDayTask(@NonNull Day day, @NonNull DayTask dayTask) {
        checkNotNull(day, "Day cannot be null");
        checkArgument(day.getId().isPresent(), "Day is not a reference");
        checkNotNull(dayTask, "Task cannot be null");
        addDayTask(day.getId().get(), dayTask);
    }

    @Override
    public void getDayTask(@NonNull Day day, @NonNull String taskId) {

    }

    @Override
    public void getDayTask(@NonNull String dayId, @NonNull String taskId) {

    }

    @Override
    public void removeDayTask(@NonNull Day day, @NonNull String taskId) {

    }

    @Override
    public void removeDayTask(@NonNull Day day, @NonNull Task task) {

    }

    @Override
    public void removeDayTask(@NonNull String dayId, @NonNull String taskId) {

    }

    @Override
    public void removeDayTask(@NonNull String dayId, @NonNull Task task) {

    }

    @Override
    public void completeTaskInDay(@NonNull Day day, @NonNull Task task) {

    }

    @Override
    public void completeTaskInDay(@NonNull Day day, @NonNull String taskId) {

    }

    @Override
    public void completeTaskInDay(@NonNull String dayId, @NonNull Task task) {

    }

    @Override
    public void completeTaskInDay(@NonNull String dayId, @NonNull String taskId) {

    }

    @Deprecated
    @Override
    public void setDayCurrentTask(@NonNull String dayId, @NonNull final String taskId) {

    }

    @Override
    public void setDayCurrentTask(@NonNull final String dayId, @NonNull final DayTask dayTask) {
        if(dayTask.getAccumulatedTime() != 0) {
            final DatabaseReference dayReference = mDatabaseReference.child(dayId);

            DatabaseReference dayCurrentTaskReference = dayReference.child("currentTask");

            dayCurrentTaskReference.setValue(dayTask.getId().get());

            dayReference.child("tasks").child(dayTask.getId().get()).child("state").setValue(dayTask.getState().ordinal());
            dayReference.child("tasks").child(dayTask.getId().get()).child("accumulatedTime").setValue(dayTask.getTotalTime().get());
            if(dayTask.getLastStartTime().get() == Long.MIN_VALUE) {
                dayReference.child("lastStartTime").removeValue();
            } else {
                dayReference.child("tasks").child(dayTask.getId().get()).child("lastStartTime").setValue(dayTask.getLastStartTime().get());
            }
        }

    }

    @Override
    public void setDayCurrentTask(@NonNull Day day, @NonNull String taskId) {
        setDayCurrentTask(day.getId().get(), taskId);
    }

    @Override
    public void setDayCurrentTask(@NonNull Day day, @NonNull DayTask dayTask) {
        setDayCurrentTask(day.getId().get(), dayTask);
    }

    private LinkedHashMap<String, Day> createDaysFromSnapshot(DataSnapshot daysSnapshot) {
        Iterator<DataSnapshot> children = daysSnapshot.getChildren().iterator();
        LinkedHashMap<String, Day> days = new LinkedHashMap<>();
        while (children.hasNext()) {
            DataSnapshot dayChildSnapshot = children.next();
            Day day = createDayFromSnapshot(dayChildSnapshot);
            days.put(day.getId().get(), day);
        }
        return days;
    }

    private Day createDayFromSnapshot(DataSnapshot daySnapshot) {
        Iterator<DataSnapshot> children = daySnapshot.child("tasks").getChildren().iterator();
        ArrayList<DayTask> taskReferencesList = new ArrayList<>(Collections.nCopies((int)daySnapshot.child("tasks").getChildrenCount(), new DayTask("", DayTask.TaskState.NONE)));
        while (children.hasNext()) {
            DataSnapshot taskReferenceSnapshot = children.next();
            DayTask taskReference;
            taskReference = new DayTask(taskReferenceSnapshot.getKey(),
                    DayTask.TaskState.values()[taskReferenceSnapshot.child("state").getValue(Integer.class)],
                    taskReferenceSnapshot.child("order").getValue(Integer.class),
                    taskReferenceSnapshot.child("lastStartTime").exists() ? taskReferenceSnapshot.child("lastStartTime").getValue(Long.class) : 0,
                    taskReferenceSnapshot.child("accumulatedTime").exists() ? taskReferenceSnapshot.child("accumulatedTime").getValue(Long.class) : 0);
            taskReferencesList.set(taskReference.getOrder().get(), taskReference);
        }
        LinkedHashMap<String, DayTask> taskReferences = new LinkedHashMap<>();
        for (DayTask dayTask : taskReferencesList) taskReferences.put(dayTask.getId().get(),dayTask);
        Day day;
        if(taskReferencesList.size() > 0) {
            day = new Day(daySnapshot.child("date").getValue(Long.class), daySnapshot.getKey(), taskReferences.get(daySnapshot.child("currentTask").getValue(String.class)), taskReferences);
        } else {
            day = new Day(daySnapshot.child("date").getValue(Long.class), daySnapshot.getKey());
        }
        return day;
    }
}
