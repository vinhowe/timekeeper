/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.schedule;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.base512.accountant.data.source.user.UserRepository;
import com.base512.accountant.tasks.TasksAdapter;
import com.base512.accountant.views.OnDragStartListener;
import com.base512.accountant.views.SimpleItemTouchHelperCallback;

import dagger.Module;
import dagger.Provides;

@Module
public class SchedulePresenterModule {
    private final ScheduleContract.View mView;

    public SchedulePresenterModule(ScheduleContract.View view) {
        mView = view;
    }

    @Provides
    ScheduleContract.View provideScheduleContractView() {
        return mView;
    }

    @Provides
    TasksAdapter provideUpcomingTasksAdapter(UserRepository userRepository) {
        return new TasksAdapter(userRepository);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

}
