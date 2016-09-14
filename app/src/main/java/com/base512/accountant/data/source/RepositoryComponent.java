package com.base512.accountant.data.source;

import com.base512.accountant.ApplicationComponent;
import com.base512.accountant.ApplicationModule;
import com.base512.accountant.data.source.days.DaysRepository;
import com.base512.accountant.data.source.days.DaysRepositoryComponent;
import com.base512.accountant.data.source.days.DaysRepositoryModule;
import com.base512.accountant.data.source.tasks.TasksRepository;
import com.base512.accountant.data.source.tasks.TasksRepositoryComponent;
import com.base512.accountant.data.source.tasks.TasksRepositoryModule;
import com.base512.accountant.data.source.user.UserRepository;
import com.base512.accountant.data.source.user.UserRepositoryModule;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TasksRepositoryModule.class, DaysRepositoryModule.class, RepositoryModule.class, UserRepositoryModule.class, ApplicationModule.class})
public interface RepositoryComponent {
    DaysRepository getDaysRepository();

    TasksRepository getTasksRepository();

    UserRepository getUserRepository();

    DatabaseReference getDatabaseReference();
}
