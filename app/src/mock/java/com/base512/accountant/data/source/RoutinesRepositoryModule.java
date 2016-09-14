package com.base512.accountant.data.source;

import android.content.Context;

import com.base512.accountant.data.FakeTasksRemoteDataSource;
import com.base512.accountant.data.source.tasks.TasksDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is used by Dagger to inject the required arguments into the {@link com.base512.accountant.data.source.tasks.TasksRepository}.
 */
@Module
public class TasksRepositoryModule {


    @Singleton
    @Provides
    TasksDataSource provideTasksDataSource() {
        return new FakeTasksRemoteDataSource();
    }

}
