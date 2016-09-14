/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.day;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.Day;
import com.base512.accountant.data.DayTask;
import com.base512.accountant.data.Task;
import com.base512.accountant.data.source.days.DaysDataSource;
import com.base512.accountant.data.source.days.DaysRepository;
import com.base512.accountant.tasks.TasksAdapter;
import com.base512.accountant.data.source.tasks.TasksDataSource;
import com.base512.accountant.data.source.tasks.TasksRepository;
import com.base512.accountant.util.TimeUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.inject.Inject;

public class DayPresenter implements DayContract.Presenter {

    private final DayContract.View mDayView;
    private final DaysRepository mDaysRepository;
    private final TasksRepository mTasksRepository;
    private final TasksAdapter mTasksAdapter;
    private final LinearLayoutManager mLinearLayoutManager;

    private boolean mFirstLoad = true;

    private Day mCurrentDay;

    private final Runnable mTimeUpdateRunnable = new TimeUpdateRunnable();

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    DayPresenter(DaysRepository daysRepository, TasksRepository tasksRepository, DayContract.View dayView, TasksAdapter tasksAdapter, LinearLayoutManager linearLayoutManager) {
        mDaysRepository = daysRepository;
        mTasksRepository = tasksRepository;
        mDayView = dayView;
        mTasksAdapter = tasksAdapter;
        mLinearLayoutManager = linearLayoutManager;
    }

    @Inject
    void setupListeners() {
        mDayView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadDay(boolean forceUpdate) {
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {

    }

    @Override
    public void completeTask(@NonNull Task completedTask) {

    }

    @Override
    public void skipTask(@NonNull Task skippedTask) {

    }

    @Override
    public void activateTask(@NonNull Task activeTask) {

    }

    @Override
    public void setCurrentTask(@NonNull DayTask dayTask, @NonNull Task task) {
        mCurrentDay = mCurrentDay.setCurrentTask(dayTask.start());
        mDayView.showCurrentTask(task);
    }

    @Override
    public void start() {
        mDayView.setLayoutManager(mLinearLayoutManager);
        loadDay(false);
    }

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mDayView.setLoadingIndicator(true);
        }
        Log.d(DayPresenter.class.getSimpleName(), "loading tasks");

        mTasksRepository.getTasks(new TasksDataSource.LoadDataCallback() {
            @Override
            public void onDataLoaded(final LinkedHashMap<String, DataObject> tasks) {
                final ArrayList<Task> tasksToShow = new ArrayList<>();
                mDaysRepository.getDayByDate(TimeUtils.getMidnightTime(), new DaysDataSource.GetDataCallback() {
                    @Override
                    public void onDataLoaded(DataObject dataObject) {
                        Day day = (Day) dataObject;
                        if(day.getTasks().size() > 0) {
                            for (DayTask task : day.getTasks().values()) {
                                tasksToShow.add((Task)tasks.get(task.getId().get()));
                            }
                            mCurrentDay = day;
                            if(mCurrentDay.getCurrentTask().isPresent()) {
                                setCurrentTask(day.getCurrentTask().get(), (Task) tasks.get(day.getCurrentTask().get().getId().get()));
                            } else {
                                setCurrentTask(new ArrayList<>(day.getTasks().values()).get(0), tasksToShow.get(0));
                            }
                            mTasksAdapter.updateTasksList(tasksToShow, false);
                            mDayView.setUpcomingTaskAdapter(mTasksAdapter);
                            startUpdatingTime();
                        } else {
                            mDayView.setNoUpcomingTasksIndicator(true);
                        }
                    }

                    @Override
                    public void onDataError() {
                        mDaysRepository.saveDay(new Day(TimeUtils.getMidnightTime()), new DaysDataSource.UpdateDataCallback() {
                            @Override
                            public void onDataUpdated(String id) {
                                loadDay(mFirstLoad);
                            }

                            @Override
                            public void onDataError() {
                                Log.e(DayPresenter.class.getSimpleName(), "Couldn't save new day");
                            }
                        });
                    }
                });
                //mDaysRepository.getDayByDate

                // The view may not be able to handle UI updates anymore
                if (!mDayView.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mDayView.setLoadingIndicator(false);
                }

                //processTasks(tasksToShow);
            }

            @Override
            public void onDataError() {

            }
        });
    }

    @Override
    public void updateTime() {
        if (mCurrentDay == null) {
            start();
            return;
        }
        if (mCurrentDay.getCurrentTask().isPresent() && mCurrentDay.getCurrentTask().get().getTotalTime().isPresent()) {
            final long totalTime = mCurrentDay.getCurrentTask().get().getTotalTime().get();

            mDayView.setTimeElapsed(totalTime);
        }
    }

    @Override
    public void startUpdatingTime() {
        mDayView.updateTimingRunnable(mTimeUpdateRunnable, 0);
    }

    @Override
    public void stopUpdatingTime() {
        mDayView.removeTimingRunnable(mTimeUpdateRunnable);
    }

    @Override
    public DayTask getCurrentTask() {
        if(mCurrentDay != null) {
            return mCurrentDay.getCurrentTask().get();
        }
        return null;
    }

    @Override
    public void saveDay() {
        mDaysRepository.setDayCurrentTask(mCurrentDay, mCurrentDay.getCurrentTask().get());
    }

    @Override
    public void pauseTask() {
        mCurrentDay.setCurrentTask(mCurrentDay.getCurrentTask().get().pause());
    }

    private final class TimeUpdateRunnable implements Runnable {

        @Override
        public void run() {
            final long startTime = SystemClock.elapsedRealtime();

            updateTime();

            if (mCurrentDay.getCurrentTask().get().isRunning()) {

                final int period = 25;

                final long endTime = SystemClock.elapsedRealtime();
                final long delay = Math.max(0, startTime + period - endTime);

                mDayView.updateTimingRunnable(this, delay);
            }
        }
    }
}
