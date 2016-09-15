/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.day;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.base512.accountant.BasePresenter;
import com.base512.accountant.BaseView;
import com.base512.accountant.data.Day;
import com.base512.accountant.data.DayTask;
import com.base512.accountant.data.Task;

import java.util.List;

public interface DayContract {
    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showCurrentTask(Task task);

        void setUpcomingTaskAdapter(RecyclerView.Adapter<?> taskAdapter);

        void showDayTaskDetailsUI(String taskId);

        void showLoadingUpcomingTasksError();

        void setNoTasksIndicator(boolean visible);

        void setLayoutManager(RecyclerView.LayoutManager layoutManager);

        boolean isActive();

        void setTimeElapsed(long totalTime);

        void updateTimingRunnable(Runnable timeUpdateRunnable, long delay);

        void removeTimingRunnable(Runnable timeUpdateRunnable);

        void setPaused(boolean b);
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadDay(boolean forceUpdate);

        void openTaskDetails(@NonNull Task requestedTask);

        void completeTask(@NonNull Task completedTask);

        void skipTask(@NonNull Task skippedTask);

        void activateTask(@NonNull Task activeTask);

        void setCurrentTask(@NonNull DayTask dayTask, @NonNull Task task);

        void updateTime();

        void startUpdatingTime();

        void stopUpdatingTime();

        DayTask getCurrentTask();

        void saveDay();

        void pauseTask();

        void completeTask();

        void skipTask();

        void showNextTask();
    }
}
