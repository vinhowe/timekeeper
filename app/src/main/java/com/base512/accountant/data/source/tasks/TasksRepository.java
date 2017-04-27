/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.data.source.tasks;

import android.support.annotation.NonNull;
import android.util.Log;

import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.DynamicTask;
import com.base512.accountant.data.Routine;
import com.base512.accountant.data.Schedule;
import com.base512.accountant.data.Task;
import com.base512.accountant.data.source.schedules.SchedulesRepository;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TasksRepository implements TasksDataSource {

    private final DatabaseReference mDatabaseReference;
    private final String TAG = TasksRepository.class.getSimpleName();
    private final SchedulesRepository mSchedulesRepository;

    @Inject
    public TasksRepository(DatabaseReference databaseReference, SchedulesRepository schedulesRepository) {
        mDatabaseReference = databaseReference.child("tasks");
        mSchedulesRepository = schedulesRepository;
    }

    @Override
    public void getTasks(@NonNull final LoadDataCallback callback) {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, DataObject> tasks = (LinkedHashMap<String, DataObject>)(LinkedHashMap) getTasksFromSnapshot(dataSnapshot);
                callback.onDataLoaded(tasks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataError();
            }
        });
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull final GetDataCallback<Task> callback) {
        mDatabaseReference.child(taskId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getTaskFromSnapshot(dataSnapshot, new GetDataCallback<Task>() {
                    @Override
                    public void onDataLoaded(Task data) {
                        callback.onDataLoaded(data);
                    }

                    @Override
                    public void onDataError() {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataError();
            }
        });
    }

    @Override
    public void getTasksFromRoutine(@NonNull String routineId, @NonNull final GetDataCallback<Task> callback) {

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

    @Override
    public void setTaskSchedule(@NonNull String taskId, @NonNull Schedule schedule) {

    }

    private LinkedHashMap<String, Task> getTasksFromSnapshot(DataSnapshot tasksSnapshot) {
        Iterator<DataSnapshot> children = tasksSnapshot.getChildren().iterator();
        final LinkedHashMap<String, Task> tasks = new LinkedHashMap<>();
        while (children.hasNext()) {
            DataSnapshot taskSnapshot = children.next();
            getTaskFromSnapshot(taskSnapshot, new GetDataCallback<Task>() {
                @Override
                public void onDataLoaded(Task task) {
                    if(task != null) {
                        tasks.put(task.getId().get(), task);
                    }
                }

                @Override
                public void onDataError() {

                }
            });

        }

        return tasks;
    }

    private void getTaskFromSnapshot(final DataSnapshot taskSnapshot, final GetDataCallback<Task> callback) {
        final Task task;
        if(taskSnapshot.child("dynamic").exists()) {
            task = new DynamicTask(taskSnapshot.child("label").getValue(String.class), taskSnapshot.getKey(), taskSnapshot.child("duration").getValue(Integer.class), null);
            callback.onDataLoaded(task);
        } else {
            // System.out.println(taskSnapshot.getValue());
            if(taskSnapshot.child("tasks").exists()) {
                final ArrayList<com.google.android.gms.tasks.Task<Task>> loadTasks = new ArrayList<>();
                for(DataSnapshot childTask : taskSnapshot.child("tasks").getChildren()) {

                    // Task for listening for task and schedule
                    final TaskCompletionSource<Task> conditionalTaskTaskCompletionSource = new TaskCompletionSource<>();
                    final com.google.android.gms.tasks.Task<Task> conditionalTaskLoadTask = conditionalTaskTaskCompletionSource.getTask();

                    if(childTask.child("schedule").exists()) {
                        // Task for loading schedule
                        final TaskCompletionSource<Schedule> scheduleTaskCompletionSource = new TaskCompletionSource<>();
                        final com.google.android.gms.tasks.Task<Schedule> scheduleLoadTask = scheduleTaskCompletionSource.getTask();

                        // Task for loading task
                        final TaskCompletionSource<Task> taskTaskCompletionSource = new TaskCompletionSource<>();
                        final com.google.android.gms.tasks.Task<Task> taskLoadTask = taskTaskCompletionSource.getTask();

                        Tasks.whenAll(scheduleLoadTask, taskLoadTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Task conditionalTask = new Task(taskLoadTask.getResult(), scheduleLoadTask.getResult());
                                conditionalTaskTaskCompletionSource.setResult(conditionalTask);
                            }
                        });
                        // TODO Add support for multiple schedules
                        mSchedulesRepository.getSchedule(childTask.child("schedule").child(String.valueOf(0)).getValue(String.class), new GetDataCallback<Schedule>() {
                            @Override
                            public void onDataLoaded(Schedule schedule) {
                                scheduleTaskCompletionSource.setResult(schedule);
                            }

                            @Override
                            public void onDataError() {
                                // TODO Figure out better error handling for data objects
                                scheduleTaskCompletionSource.setException(new Exception("Couldn't load data"));
                            }
                        });

                        getTask(childTask.child("id").getValue(String.class), new GetDataCallback<Task>() {
                            @Override
                            public void onDataLoaded(Task task) {
                                taskTaskCompletionSource.setResult(task);
                            }

                            @Override
                            public void onDataError() {
                                // TODO Figure out better error handling for data objects
                                taskTaskCompletionSource.setException(new Exception("Couldn't load data"));
                            }
                        });
                    } else {
                        Log.d(TAG, childTask.getKey());
                        getTask(childTask.child("id").getValue(String.class), new GetDataCallback<Task>() {
                            @Override
                            public void onDataLoaded(Task task) {
                                conditionalTaskTaskCompletionSource.setResult(task);
                            }

                            @Override
                            public void onDataError() {
                                // TODO Figure out better error handling for data objects
                                conditionalTaskTaskCompletionSource.setException(new Exception("Couldn't load data"));
                            }
                        });
                    }

                    loadTasks.add(conditionalTaskLoadTask);
                }
                Tasks.whenAll(loadTasks).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "Today is" + (tasks.get(0).getResult().checkScheduleForDate(DateTime.now()) ? " " : " NOT ") + "scheduled");
                        LinkedHashMap<String, Task> childConditionalTasks = new LinkedHashMap<>();

                        for(com.google.android.gms.tasks.Task<Task> loadTask : loadTasks) {
                            childConditionalTasks.put(loadTask.getResult().getId().get(), loadTask.getResult());
                        }
                        // TODO Don't know if this needs a schedule object
                        callback.onDataLoaded(new Routine(taskSnapshot.child("label").getValue(String.class), taskSnapshot.getKey(), taskSnapshot.child("duration").getValue(Integer.class), childConditionalTasks, null));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Couldn't load task");
                    }
                });
            } else {
                task = new Task(taskSnapshot.child("label").getValue(String.class), taskSnapshot.getKey(), taskSnapshot.child("duration").getValue(Integer.class), null);
                callback.onDataLoaded(task);
            }
        }
    }

}
