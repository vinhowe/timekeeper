/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant.day;

import com.base512.accountant.ApplicationModule;
import com.base512.accountant.data.source.RepositoryComponent;
import com.base512.accountant.data.source.days.DaysRepository;
import com.base512.accountant.data.source.days.DaysRepositoryComponent;
import com.base512.accountant.data.source.tasks.TasksRepositoryComponent;
import com.base512.accountant.util.FragmentScoped;

import dagger.Component;

@FragmentScoped
@Component(dependencies = RepositoryComponent.class, modules = {DayPresenterModule.class, ApplicationModule.class})
public interface DayComponent {

    void inject(DayActivity activity);
}