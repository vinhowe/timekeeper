/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */

package com.base512.accountant.data.source.tasks;

import android.support.annotation.NonNull;

import com.base512.accountant.data.Task;
import com.base512.accountant.data.source.AccessDataCallback;
import com.base512.accountant.data.source.BaseDataSource;

import java.util.LinkedHashMap;

public interface TasksDataSource extends BaseDataSource {

    void getTasks(@NonNull LoadDataCallback callback);

    void getTask(@NonNull String taskId, @NonNull GetDataCallback callback);

    void saveTask(@NonNull Task task, @NonNull UpdateDataCallback callback);

    void setTaskLabel(@NonNull String taskId, @NonNull String label);

    void setTaskDynamic(@NonNull String taskId, boolean isDynamic);

    void setTaskDuration(@NonNull String taskId, int duration);
}
