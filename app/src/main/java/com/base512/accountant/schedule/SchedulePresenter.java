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
import android.view.View;
import android.widget.AdapterView;

import com.base512.accountant.data.ConditionalTask;
import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.Day;
import com.base512.accountant.data.DayTask;
import com.base512.accountant.data.Routine;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.inject.Inject;

public class SchedulePresenter implements ScheduleContract.Presenter, OnDragStartListener, AdapterView.OnItemSelectedListener{

    private final ScheduleContract.View mScheduleView;
    private final DaysRepository mDaysRepository;
    private final UserRepository mUserRepository;
    private final TasksRepository mTasksRepository;
    private final TasksAdapter mTasksAdapter;
    private final LinearLayoutManager mLinearLayoutManager;
    private final SimpleItemTouchHelperCallback mItemTouchCallback;

    private Day mCurrentDay;

    private boolean mFirstLoad = true;

    private LinkedHashMap<String, DayTask> mDayTasks;

    private String TAG = SchedulePresenter.class.getSimpleName();

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
                    //mDaysRepository.addDayTask(mCurrentDay, new DayTask(id, DayTask.TaskState.NONE));\
                    mTasksAdapter.mTasks.add(new ConditionalTask(new Task(task.getLabel(), id, task.getDuration())));
                    mTasksAdapter.notifyDataSetChanged();
                    saveTasks();
                    //loadSchedule(true);
                }

                @Override
                public void onDataError() {

                }
            });
        } else {
            //mDaysRepository.addDayTask(mCurrentDay, new DayTask(task));
            mTasksAdapter.mTasks.add(new ConditionalTask(task));
            mTasksAdapter.notifyDataSetChanged();
            saveTasks();
            //loadSchedule(true);
        }
    }

    @Override
    public void removeTask(@NonNull Task task) {

    }

    @Override
    public void createTask() {
        mTasksRepository.getTasks(new TasksDataSource.LoadDataCallback<Task>() {
            @Override
            public void onDataLoaded(LinkedHashMap<String, Task> data) {
                TaskDialogFragment taskDialogFragment = TaskDialogFragment.newInstance(data.values().toArray(new Task[data.size()]));
                mScheduleView.showAddTaskUI(taskDialogFragment);
            }

            @Override
            public void onDataError() {

            }
        });

    }

    @Override
    public void initializeSchedules() {
        mScheduleView.setSpinnerItemSelectedListener(this);
        String[] items = new String[]{"Today", "Base Day", "Wakeup"};
        mScheduleView.setSpinnerItems(items);
    }

    @Override
    public void setRoutine(@NonNull final String routineId) {
        mTasksRepository.getTasks(new BaseDataSource.LoadDataCallback<Task>() {
            @Override
            public void onDataLoaded(LinkedHashMap<String, Task> data) {
                mTasksRepository.getTask(routineId, new BaseDataSource.GetDataCallback() {
                    @Override
                    public void onDataLoaded(DataObject data) {
                        Log.d(SchedulePresenter.class.getSimpleName(), "Loaded new routine");
                    }

                    @Override
                    public void onDataError() {

                    }
                });
            }

            @Override
            public void onDataError() {

            }
        });
    }

    @Override
    public void saveTasks() {
        LinkedHashMap<String, DayTask> tasks = new LinkedHashMap<>();
        for(ConditionalTask task : mTasksAdapter.mTasks) {
            tasks.put(task.getTask().getId().get(), new DayTask(task.getTask().getId().get(), DayTask.TaskState.NONE));
        }
        mCurrentDay = new Day(mCurrentDay.getDate(), mCurrentDay.getId().get(), tasks);
        LinkedHashMap<String, DayTask> updatedDayTasks = new LinkedHashMap<>();
        for (int i = 0; i < mTasksAdapter.mTasks.size(); i++) {
            if(mDayTasks.containsKey(mTasksAdapter.mTasks.get(i).getTask().getId().get())) {
                updatedDayTasks.put(mTasksAdapter.mTasks.get(i).getTask().getId().get(), mDayTasks.get(mTasksAdapter.mTasks.get(i).getTask().getId().get()).withOrder(i));
            } else {
                updatedDayTasks.put(mTasksAdapter.mTasks.get(i).getTask().getId().get(), new DayTask(mTasksAdapter.mTasks.get(i).getTask()).withOrder(i));
            }
        }
        mDaysRepository.updateDayTasks(mCurrentDay, new ArrayList<>(mDayTasks.values()), updatedDayTasks, new BaseDataSource.UpdateDataCallback() {
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
        initializeSchedules();
    }

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mScheduleView.setLoadingIndicator(true);
        }
        Log.d(SchedulePresenter.class.getSimpleName(), "loading tasks");

        mTasksRepository.getTask("baseDay", new BaseDataSource.GetDataCallback<Task>() {
            @Override
            public void onDataLoaded(Task task) {
                if(task instanceof Routine) {

                    Routine routine = (Routine)task;

                    ArrayList<ConditionalTask> tasksToShow = new ArrayList<>();

                    Log.d(TAG, "baseDay is routine");
                    Log.d(TAG, String.valueOf(routine.getConditionalTasks().values().size()));

                    for(ConditionalTask childTask : routine.getConditionalTasks().values()) {
                        tasksToShow.add(childTask);
                    }
                    mTasksAdapter.updateTasksList(tasksToShow, true);
                    mScheduleView.setTasksAdapter(mTasksAdapter);
                } else {
                    Log.d(TAG, "baseDay is not routine");
                }
            }

            @Override
            public void onDataError() {

            }
        });

        // TODO Make editing solved day schedule possible
/*        mTasksRepository.getTasks(new TasksDataSource.LoadDataCallback<Task>() {
            @Override
            public void onDataLoaded(final LinkedHashMap<String, Task> tasks) {
                final ArrayList<Task> tasksToShow = new ArrayList<>();
                mDaysRepository.getDayByDate(TimeUtils.getMidnightTime(), new DaysDataSource.GetDataCallback() {
                    @Override
                    public void onDataLoaded(DataObject dataObject) {
                        mCurrentDay = (Day) dataObject;
                        if (mCurrentDay.getTasks().size() > 0) {
                            mDayTasks = mCurrentDay.getTasks();
                            for (DayTask task : mDayTasks.values()) {
                                tasksToShow.add(tasks.get(task.getId().get()));
                            }
                            mTasksAdapter.updateTasksList(tasksToShow, true);
                            mScheduleView.setTasksAdapter(mTasksAdapter);
                        } else {
                            mDayTasks = new LinkedHashMap<>();
                            mScheduleView.setNoTasksIndicator(true);
                        }
                    }

                    @Override
                    public void onDataError() {
*//*                        mDaysRepository.saveDay(new Day(TimeUtils.getMidnightTime()), new DaysDataSource.UpdateDataCallback() {
                            @Override
                            public void onDataUpdated(String id) {
                                loadSchedule(mFirstLoad);
                            }

                            @Override
                            public void onDataError() {
                                Log.e(SchedulePresenter.class.getSimpleName(), "Couldn't save new day");
                            }
                        });*//*
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
        });*/
    }

    @Override
    public void onDragStart(RecyclerView.ViewHolder viewHolder) {
        new ItemTouchHelper(mItemTouchCallback).startDrag(viewHolder);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        setRoutine("baseDay");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
