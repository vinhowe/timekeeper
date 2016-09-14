package com.base512.accountant.schedule;

import com.base512.accountant.ApplicationModule;
import com.base512.accountant.data.source.RepositoryComponent;
import com.base512.accountant.data.source.days.DaysRepository;
import com.base512.accountant.data.source.days.DaysRepositoryComponent;
import com.base512.accountant.data.source.tasks.TasksRepositoryComponent;
import com.base512.accountant.util.FragmentScoped;

import dagger.Component;

@FragmentScoped
@Component(dependencies = RepositoryComponent.class, modules = {SchedulePresenterModule.class, ApplicationModule.class})
public interface ScheduleComponent {

    void inject(ScheduleActivity activity);
}