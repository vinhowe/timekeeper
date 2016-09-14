/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.day;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.base512.accountant.data.source.user.UserRepository;
import com.base512.accountant.tasks.TasksAdapter;

import dagger.Module;
import dagger.Provides;

@Module
public class DayPresenterModule {
    private final DayContract.View mView;

    public DayPresenterModule(DayContract.View view) {
        mView = view;
    }

    @Provides
    DayContract.View provideDayContractView() {
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
