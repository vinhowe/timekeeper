/**
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, August 2016
 */

package com.base512.accountant.data.source.user;

import android.support.annotation.NonNull;

import com.base512.accountant.data.Task;
import com.base512.accountant.data.source.BaseDataSource;

import org.joda.time.LocalTime;

public interface UserDataSource extends BaseDataSource {

    void getUser(@NonNull GetDataCallback callback);

    void setUserWakeupTime(@NonNull int time);
}
