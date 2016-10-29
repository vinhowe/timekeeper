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
import android.widget.Toast;

import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.Day;
import com.base512.accountant.data.DayTask;
import com.base512.accountant.data.Task;
import com.base512.accountant.data.source.BaseDataSource;
import com.base512.accountant.data.source.days.DaysDataSource;
import com.base512.accountant.data.source.days.DaysRepository;
import com.base512.accountant.tasks.TasksAdapter;
import com.base512.accountant.data.source.tasks.TasksDataSource;
import com.base512.accountant.data.source.tasks.TasksRepository;
import com.base512.accountant.util.TimeUtils;
import com.google.common.base.Optional;

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
        Log.d(DayPresenter.class.getSimpleName(), dayTask.getState().toString());
        mCurrentDay = mCurrentDay.setCurrentTask((dayTask.getState() == DayTask.TaskState.NONE) ? dayTask.start() : dayTask);
        if(dayTask.getState() == DayTask.TaskState.PAUSED) {
            mDayView.setPaused(true);
            // Update time once to show current paused time
            updateTime();
            stopUpdatingTime();
            saveDay();
        } else if (dayTask.getState() == DayTask.TaskState.COMPLETE || dayTask.getState() == DayTask.TaskState.SKIPPED) {
            showNextTask();
        } else {
            Toast.makeText(((DayFragment)(mDayView)).getContext(), "continuing task", Toast.LENGTH_LONG).show();
            mDayView.setPaused(false);
            startUpdatingTime();
            saveDay();
        }

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
                final LinkedHashMap<String, Task> tasksToShow = new LinkedHashMap<>();
                mDaysRepository.getDayByDate(TimeUtils.getMidnightTime(), new DaysDataSource.GetDataCallback() {
                    @Override
                    public void onDataLoaded(DataObject dataObject) {
                        Day day = (Day) dataObject;
                        if(day.getTasks().size() > 0) {
                            for (DayTask dayTask : day.getTasks().values()) {
                                Task task = (Task)tasks.get(dayTask.getId().get());
                                tasksToShow.put(task.getId().get(), task);
                            }
                            mCurrentDay = day;
                            if(mCurrentDay.getCurrentTask().isPresent()) {
                                setCurrentTask(day.getCurrentTask().get(), (Task) tasks.get(day.getCurrentTask().get().getId().get()));
                                tasksToShow.remove(day.getCurrentTask().get().getId().get());
                                mTasksAdapter.updateTasksList(new ArrayList(tasksToShow.values()), false);
                            } else {
                                setCurrentTask(new ArrayList<>(day.getTasks().values()).get(0), new ArrayList<>(tasksToShow.values()).get(0));
                                ArrayList upcomingTasks = new ArrayList(tasksToShow.values());
                                mTasksAdapter.updateTasksList(new ArrayList<>(upcomingTasks.subList(1,tasksToShow.size())), false);
                            }
                            mTasksAdapter.updateTasksList(new ArrayList(tasksToShow.values()), false);
                            mDayView.setUpcomingTaskAdapter(mTasksAdapter);
                            mDayView.setNoTasksIndicator(false, false);
                        } else {
                            if(!day.getState().equals(Day.State.COMPLETE)) {
                                mDayView.showSchedule();
                            }
                            mDayView.setNoTasksIndicator(true, false);
                        }
                    }

                    @Override
                    public void onDataError() {
                        mDaysRepository.getToday(new DaysDataSource.GetDataCallback() {

                            @Override
                            public void onDataError() {
                                Log.e(DayPresenter.class.getSimpleName(), "Couldn't get today");
                            }

                            @Override
                            public void onDataLoaded(DataObject data) {
                                loadDay(mFirstLoad);
                            }
                        });
/*                        mDaysRepository.saveDay(new Day(TimeUtils.getMidnightTime()), new DaysDataSource.UpdateDataCallback() {
                            @Override
                            public void onDataUpdated(String id) {
                                loadDay(mFirstLoad);
                            }

                            @Override
                            public void onDataError() {
                                Log.e(DayPresenter.class.getSimpleName(), "Couldn't save new day");
                            }
                        });*/
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
        if (!getCurrentDay().isPresent()) {
            return;
        }
        if (getCurrentDay().get().getCurrentTask().isPresent() && getCurrentDay().get().getCurrentTask().get().getTotalTime().isPresent()) {
            final long totalTime = getCurrentDay().get().getCurrentTask().get().getTotalTime().get();

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
        if(getCurrentDay().isPresent()) {
            return getCurrentDay().get().getCurrentTask().get();
        }
        return null;
    }

    @Override
    public void saveDay() {
        if(getCurrentDay().isPresent() && getCurrentDay().get().getCurrentTask().isPresent()) {
            mDaysRepository.setDayCurrentTask(getCurrentDay().get(), getCurrentDay().get().getCurrentTask().get());
            mDaysRepository.setDayState(getCurrentDay().get().getId().get(), getCurrentDay().get().getState());
        }
    }

    @Override
    public void pauseTask() {
        if(getCurrentDay().isPresent() && getCurrentDay().get().getCurrentTask().isPresent()) {
            if(getCurrentDay().get().getCurrentTask().get().getState().equals(DayTask.TaskState.RUNNING)) {
                mCurrentDay = getCurrentDay().get().setCurrentTask(getCurrentDay().get().getCurrentTask().get().pause());
                mDayView.setPaused(true);
                stopUpdatingTime();
            } else {
                mCurrentDay = getCurrentDay().get().setCurrentTask(getCurrentDay().get().getCurrentTask().get().start());
                mDayView.setPaused(false);
                startUpdatingTime();
            }

            //mDaysRepository.setDayCurrentTask(getCurrentDay().get(), getCurrentDay().get().getCurrentTask().get());
            saveDay();
        }
    }

    @Override
    public void completeTask() {
        if(getCurrentDay().isPresent() && getCurrentDay().get().getCurrentTask().isPresent()) {
            mCurrentDay = getCurrentDay().get().setCurrentTask(getCurrentDay().get().getCurrentTask().get().complete());
            saveDay();
            //mDaysRepository.setDayCurrentTask(getCurrentDay().get(), getCurrentDay().get().getCurrentTask().get());
            showNextTask();
        }
    }

    @Override
    public void skipTask() {
        if(getCurrentDay().isPresent() && getCurrentDay().get().getCurrentTask().isPresent()) {
            mCurrentDay = getCurrentDay().get().setCurrentTask(getCurrentDay().get().getCurrentTask().get().skip());
            //mDaysRepository.setDayCurrentTask(getCurrentDay().get(), getCurrentDay().get().getCurrentTask().get());
            showNextTask();
            saveDay();
        }
    }

    @Override
    public void showNextTask() {
        stopUpdatingTime();
        if(getCurrentDay().isPresent() && getCurrentDay().get().getCurrentTask().isPresent()) {
            mDaysRepository.getDayByDate(TimeUtils.getMidnightTime(), new BaseDataSource.GetDataCallback() {
                @Override
                public void onDataLoaded(DataObject data) {
                    Day today = (Day) data;
                    for(final DayTask dayTask : today.getTasks().values()) {
                        if(dayTask.getState() == DayTask.TaskState.NONE) {
                            mTasksRepository.getTask(dayTask.getId().get(), new TasksDataSource.GetDataCallback() {

                                @Override
                                public void onDataLoaded(DataObject data) {
                                    setCurrentTask(dayTask.start(), (Task) data);
                                    startUpdatingTime();
                                }

                                @Override
                                public void onDataError() {

                                }
                            });
                            mDayView.setNoTasksIndicator(false, false);
                            return;
                        }
                    }
                    mCurrentDay = getCurrentDay().get().complete().get();
                    saveDay();
                    mDayView.setNoTasksIndicator(true, true);
                }

                @Override
                public void onDataError() {

                }
            });
        }

        return;
    }

    public Optional<Day> getCurrentDay() {
        return mCurrentDay == null ? Optional.<Day>absent() : Optional.of(mCurrentDay);
    }

    private final class TimeUpdateRunnable implements Runnable {

        @Override
        public void run() {
            final long startTime = SystemClock.elapsedRealtime();

            updateTime();

            if (getCurrentDay().isPresent() && getCurrentDay().get().getCurrentTask().get().isRunning()) {

                final int period = 25;

                final long endTime = SystemClock.elapsedRealtime();
                final long delay = Math.max(0, startTime + period - endTime);

                mDayView.updateTimingRunnable(this, delay);
            }
        }
    }
}
