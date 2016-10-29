/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.data.source.tasks;

import android.support.annotation.NonNull;

import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.DayTask;
import com.base512.accountant.data.DynamicTask;
import com.base512.accountant.data.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.base512.accountant.util.ArrayUtils.sortByValue;

@Singleton
public class TasksRepository implements TasksDataSource {

    private final DatabaseReference mDatabaseReference;

    @Inject
    public TasksRepository(DatabaseReference databaseReference) {
        mDatabaseReference = databaseReference.child("tasks");
    }

    @Override
    public void getTasks(@NonNull final LoadDataCallback callback) {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, DataObject> tasks = (LinkedHashMap<String, DataObject>)(LinkedHashMap)getTasksFromSnapshot(dataSnapshot);
                callback.onDataLoaded(tasks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataError();
            }
        });
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull final GetDataCallback callback) {
        mDatabaseReference.child(taskId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Task task = getTaskFromSnapshot(dataSnapshot);
                callback.onDataLoaded(task);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataError();
            }
        });
    }

    @Override
    public void saveTask(@NonNull final Task task, final UpdateDataCallback callback) {
        final DatabaseReference newTaskReference = task.getId().isPresent() ? mDatabaseReference.child(task.getId().get()) : mDatabaseReference.push();
        newTaskReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.child("dynamic").setValue(task instanceof DynamicTask);
                if(task instanceof DynamicTask) {
                    mutableData.child("projectedDuration").setValue(((DynamicTask)task).getProjectedDuration());
                }
                mutableData.child("duration").setValue(task.getDuration());
                mutableData.child("label").setValue(task.getLabel());
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(b) {
                    callback.onDataUpdated(newTaskReference.getKey());
                } else {
                    callback.onDataError();
                }
            }
        });
    }

    @Override
    public void setTaskLabel(@NonNull String taskId, @NonNull String label) {
        mDatabaseReference.child(taskId).child("label").setValue(label);
    }

    @Override
    public void setTaskDynamic(@NonNull String taskId, boolean isDynamic) {
        mDatabaseReference.child(taskId).child("dynamic").setValue(isDynamic);
    }

    @Override
    public void setTaskDuration(@NonNull String taskId, int duration) {
        mDatabaseReference.child(taskId).child("duration").setValue(duration);
    }

    private LinkedHashMap<String, Task> getTasksFromSnapshot(DataSnapshot tasksSnapshot) {
        Iterator<DataSnapshot> children = tasksSnapshot.getChildren().iterator();
        LinkedHashMap<String, Task> tasks = new LinkedHashMap<>();
        while (children.hasNext()) {
            DataSnapshot taskSnapshot = children.next();
            Task task = getTaskFromSnapshot(taskSnapshot);
            if(task != null) {
                tasks.put(task.getId().get(), task);
            }
        }

        return tasks;
    }

    private Task getTaskFromSnapshot(DataSnapshot taskSnapshot) {
        Task task;
        if(taskSnapshot.child("dynamic").exists()) {
            task = new DynamicTask(taskSnapshot.child("label").getValue(String.class), taskSnapshot.getKey(), taskSnapshot.child("duration").getValue(Integer.class));
        } else {
            System.out.println(taskSnapshot.getValue());
            if(taskSnapshot.child("tasks").exists()) {
                return null;
            }
            task = new Task(taskSnapshot.child("label").getValue(String.class), taskSnapshot.getKey(), taskSnapshot.child("duration").getValue(Integer.class));
        }
        return task;
    }
}
