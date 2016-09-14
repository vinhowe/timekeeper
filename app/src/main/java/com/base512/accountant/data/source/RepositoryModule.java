package com.base512.accountant.data.source;

import com.base512.accountant.ApplicationModule;
import com.base512.accountant.data.source.days.DaysRepositoryComponent;
import com.base512.accountant.data.source.days.DaysRepositoryModule;
import com.base512.accountant.data.source.tasks.TasksRepository;
import com.base512.accountant.data.source.tasks.TasksRepositoryComponent;
import com.base512.accountant.data.source.tasks.TasksRepositoryModule;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {
    @Provides
    DatabaseReference providesDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
