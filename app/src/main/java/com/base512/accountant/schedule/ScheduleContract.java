/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.schedule;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.base512.accountant.BasePresenter;
import com.base512.accountant.BaseView;
import com.base512.accountant.data.Routine;
import com.base512.accountant.data.Task;
import com.base512.accountant.tasks.TasksAdapter;

public interface ScheduleContract {
    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void setTasksAdapter(TasksAdapter tasksAdapter);

        void setLayoutManager(RecyclerView.LayoutManager layoutManager);

        void showLoadingTasksError();

        void setTouchHelper(ItemTouchHelper helper);

        void setNoTasksIndicator(boolean visible);

        void setSpinnerItems(String[] spinnerStrings);

        void setSpinnerItemSelectedListener(AdapterView.OnItemSelectedListener listener);

        void showAddTaskUI(TaskDialogFragment taskDialogFragment);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadSchedule(boolean forceUpdate);

        void addTask(@NonNull Task task, boolean isUnique);

        void removeTask(@NonNull Task task);

        void createTask();

        void initializeSchedules();

        void setRoutine(@NonNull String routineId);

        void saveTasks();}
}
