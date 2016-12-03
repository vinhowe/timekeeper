package com.base512.accountant.data.source.schedules;

import com.google.firebase.database.DatabaseReference;

import com.base512.accountant.ApplicationComponent;
import com.base512.accountant.ApplicationModule;

import javax.inject.Singleton;

import dagger.Subcomponent;


@Singleton
@Subcomponent(modules = {SchedulesRepositoryModule.class, ApplicationModule.class})
public interface SchedulesRepositoryComponent extends ApplicationComponent {

    SchedulesRepository getSchedulesRepository();

    DatabaseReference getDatabaseReference();
}
