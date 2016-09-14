package com.base512.accountant.data.source.user;

import com.base512.accountant.ApplicationComponent;
import com.base512.accountant.ApplicationModule;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Singleton;

import dagger.Subcomponent;


@Singleton
@Subcomponent(modules = {UserRepositoryModule.class, ApplicationModule.class})
public interface UserRepositoryComponent extends ApplicationComponent {

    UserRepository getUsersRepository();

    DatabaseReference getDatabaseReference();
}
