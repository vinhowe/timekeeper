/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */
package com.base512.accountant;

import android.app.Application;

import com.base512.accountant.data.source.DaggerRepositoryComponent;
import com.base512.accountant.data.source.RepositoryComponent;
import com.base512.accountant.data.source.RepositoryModule;
import com.base512.accountant.data.source.days.DaysRepositoryComponent;
import com.base512.accountant.data.source.days.DaysRepositoryModule;
import com.base512.accountant.data.source.tasks.TasksRepositoryComponent;
import com.base512.accountant.data.source.tasks.TasksRepositoryModule;

import net.danlew.android.joda.JodaTimeAndroid;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AccountantApplication extends Application {

/*        private UserRepositoryComponent mRepositoryComponent;
        private DaysRepositoryComponent mDaysComponent;*/

        private RepositoryComponent mRepositoryComponent;

        @Override
        public void onCreate() {
            super.onCreate();

/*            mRepositoryComponent = DaggerTasksRepositoryComponent.builder()
                    .tasksRepositoryModule(new UserRepositoryModule()).build();
            mDaysComponent = DaggerDaysRepositoryComponent.builder()
                    .daysRepositoryModule(new DaysRepositoryModule()).build();*/

            mRepositoryComponent = DaggerRepositoryComponent.builder()
                    .repositoryModule(new RepositoryModule())
                    .daysRepositoryModule(new DaysRepositoryModule())
                    .tasksRepositoryModule(new TasksRepositoryModule())
                    .build();
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/Gilroy-Light.otf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );
            JodaTimeAndroid.init(this);
        }


        public RepositoryComponent getTasksRepositoryComponent() {
            return mRepositoryComponent;
        }

    }
