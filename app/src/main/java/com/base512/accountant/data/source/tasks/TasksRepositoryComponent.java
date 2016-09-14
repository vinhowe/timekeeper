package com.base512.accountant.data.source.tasks;

import com.base512.accountant.ApplicationComponent;
import com.base512.accountant.ApplicationModule;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Subcomponent;


@Singleton
@Subcomponent(modules = {TasksRepositoryModule.class, ApplicationModule.class})
public interface TasksRepositoryComponent extends ApplicationComponent {

    TasksRepository getTasksRepository();

    DatabaseReference getDatabaseReference();
}
