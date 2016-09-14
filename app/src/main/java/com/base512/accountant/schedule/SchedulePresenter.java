/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.schedule;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.Day;
import com.base512.accountant.data.DayTask;
import com.base512.accountant.data.Task;
import com.base512.accountant.data.source.BaseDataSource;
import com.base512.accountant.data.source.days.DaysDataSource;
import com.base512.accountant.data.source.days.DaysRepository;
import com.base512.accountant.data.source.tasks.TasksDataSource;
import com.base512.accountant.data.source.tasks.TasksRepository;
import com.base512.accountant.data.source.user.UserRepository;
import com.base512.accountant.tasks.TasksAdapter;
import com.base512.accountant.util.TimeUtils;
import com.base512.accountant.views.OnDragStartListener;
import com.base512.accountant.views.SimpleItemTouchHelperCallback;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.inject.Inject;

public class SchedulePresenter implements ScheduleContract.Presenter, OnDragStartListener {

    private final ScheduleContract.View mScheduleView;
    private final DaysRepository mDaysRepository;
    private final UserRepository mUserRepository;
    private final TasksRepository mTasksRepository;
    private final TasksAdapter mTasksAdapter;
    private final LinearLayoutManager mLinearLayoutManager;
    private final SimpleItemTouchHelperCallback mItemTouchCallback;

    private Day mCurrentDay;

    private boolean mFirstLoad = true;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    SchedulePresenter(DaysRepository daysRepository,
                      TasksRepository tasksRepository,
                      UserRepository userRepository,
                      ScheduleContract.View scheduleView,
                      TasksAdapter tasksAdapter,
                      LinearLayoutManager linearLayoutManager) {
        mDaysRepository = daysRepository;
        mTasksRepository = tasksRepository;
        mUserRepository = userRepository;
        mScheduleView = scheduleView;
        mTasksAdapter = tasksAdapter;
        mLinearLayoutManager = linearLayoutManager;
        mItemTouchCallback = new SimpleItemTouchHelperCallback(tasksAdapter);
    }

    @Inject
    void setupListeners() {
        mScheduleView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadSchedule(boolean forceUpdate) {
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public void addTask(@NonNull final Task task, boolean isUnique) {
        if(isUnique) {
            mTasksRepository.saveTask(task, new BaseDataSource.UpdateDataCallback() {
                @Override
                public void onDataUpdated(String id) {
                    mDaysRepository.addDayTask(mCurrentDay, new DayTask(id, DayTask.TaskState.NONE));
                    loadSchedule(true);
                }

                @Override
                public void onDataError() {

                }
            });
        } else {
            mDaysRepository.addDayTask(mCurrentDay, new DayTask(task));
            loadSchedule(true);
        }
    }

    @Override
    public void removeTask(@NonNull Task task) {

    }

    @Override
    public void createTask() {
        mTasksRepository.getTasks(new BaseDataSource.LoadDataCallback() {
            @Override
            public void onDataLoaded(LinkedHashMap<String, DataObject> data) {
                TaskDialogFragment taskDialogFragment = TaskDialogFragment.newInstance(data.values().toArray(new Task[data.size()]));
                mScheduleView.showAddTaskUI(taskDialogFragment);
            }

            @Override
            public void onDataError() {

            }
        });

    }

    @Override
    public void saveTasks() {
        LinkedHashMap<String, DayTask> tasks = new LinkedHashMap<>();
        for(Task task : mTasksAdapter.mTasks) {
            tasks.put(task.getId().get(), new DayTask(task.getId().get(), DayTask.TaskState.NONE));
        }
        mCurrentDay = new Day(mCurrentDay.getDate(), mCurrentDay.getId().get(), tasks);
        mDaysRepository.saveDay(mCurrentDay, new BaseDataSource.UpdateDataCallback() {
            @Override
            public void onDataUpdated(String id) {

            }

            @Override
            public void onDataError() {

            }
        });
    }

    @Override
    public void start() {
        mScheduleView.setLayoutManager(mLinearLayoutManager);
        mScheduleView.setTouchHelper(new ItemTouchHelper(mItemTouchCallback));
        loadSchedule(true);
    }

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mScheduleView.setLoadingIndicator(true);
        }
        Log.d(SchedulePresenter.class.getSimpleName(), "loading tasks");

        mTasksRepository.getTasks(new TasksDataSource.LoadDataCallback() {
            @Override
            public void onDataLoaded(final LinkedHashMap<String, DataObject> tasks) {
                final ArrayList<Task> tasksToShow = new ArrayList<>();
                mDaysRepository.getDayByDate(TimeUtils.getMidnightTime(), new DaysDataSource.GetDataCallback() {
                    @Override
                    public void onDataLoaded(DataObject dataObject) {
                        mCurrentDay = (Day) dataObject;
                        if (mCurrentDay.getTasks().size() > 0) {
                            for (DayTask task : mCurrentDay.getTasks().values()) {
                                tasksToShow.add((Task) tasks.get(task.getId().get()));
                            }
                            mTasksAdapter.updateTasksList(tasksToShow, true);
                            mScheduleView.setTasksAdapter(mTasksAdapter);
                        } else {
                            mScheduleView.setNoTasksIndicator(true);
                        }
                    }

                    @Override
                    public void onDataError() {
                        mDaysRepository.saveDay(new Day(TimeUtils.getMidnightTime()), new DaysDataSource.UpdateDataCallback() {
                            @Override
                            public void onDataUpdated(String id) {
                                loadSchedule(mFirstLoad);
                            }

                            @Override
                            public void onDataError() {
                                Log.e(SchedulePresenter.class.getSimpleName(), "Couldn't save new day");
                            }
                        });
                    }
                });
                //mDaysRepository.getDayByDate

                // The view may not be able to handle UI updates anymore
                if (!mScheduleView.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mScheduleView.setLoadingIndicator(false);
                }

                //processTasks(tasksToShow);
            }

            @Override
            public void onDataError() {

            }
        });
    }

    @Override
    public void onDragStart(RecyclerView.ViewHolder viewHolder) {
        new ItemTouchHelper(mItemTouchCallback).startDrag(viewHolder);
    }
}
