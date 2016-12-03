/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */

package com.base512.accountant.data.source.schedules;

import android.support.annotation.NonNull;

import com.base512.accountant.data.Schedule;
import com.base512.accountant.data.Task;
import com.base512.accountant.data.source.BaseDataSource;

public interface SchedulesDataSource extends BaseDataSource {

    void getSchedules(@NonNull LoadDataCallback<Schedule> callback);

    void getSchedule(@NonNull String scheduleId, @NonNull GetDataCallback<Schedule> callback);

    void saveSchedule(@NonNull Schedule schedule, @NonNull UpdateDataCallback callback);

    void setScheduleLabel(@NonNull String scheduleId, @NonNull String label);
}
