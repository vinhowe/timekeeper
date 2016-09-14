package com.base512.accountant.data.source.days;

import com.base512.accountant.ApplicationComponent;
import com.base512.accountant.ApplicationModule;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Subcomponent;


@Singleton
@Subcomponent(modules = {DaysRepositoryModule.class, ApplicationModule.class})
public interface DaysRepositoryComponent extends ApplicationComponent {

    DaysRepository getDaysRepository();

    DatabaseReference getDatabaseReference();
}
