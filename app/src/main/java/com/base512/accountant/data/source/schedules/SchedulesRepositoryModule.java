/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.data.source.schedules;

import com.google.firebase.database.DatabaseReference;

import com.base512.accountant.data.source.tasks.TasksRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SchedulesRepositoryModule {
    @Singleton
    @Provides
    SchedulesRepository provideSchedulesRepository(DatabaseReference reference) {
        return new SchedulesRepository(reference);
    }
}
