package com.base512.accountant.day;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.base512.accountant.data.source.days.DaysRepository;

import javax.inject.Inject;

/**
 * Created by Thomas on 10/27/2016.
 */

public class DayService extends Service {

    @Inject
    public DayService(DaysRepository da) {

    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

/*    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case DayNotificationConstants.ACTION_COMPLETE_TASK: {
                day
                break;
            }
            case DayNotificationConstants.ACTION_SKIP_TASK: {
                DataModel.getDataModel().pauseStopwatch();
                Events.sendStopwatchEvent(R.string.action_pause, R.string.label_notification);
                break;
            }
            case DayNotificationConstants.ACTION_PAUSE_TASK: {
                DataModel.getDataModel().addLap();
                Events.sendStopwatchEvent(R.string.action_lap, R.string.label_notification);
                break;
            }
            case DayNotificationConstants.ACTION_START_TASK: {
                DataModel.getDataModel().clearLaps();
                DataModel.getDataModel().resetStopwatch();
                Events.sendStopwatchEvent(R.string.action_reset, R.string.label_notification);
                break;
            }
        }

        return Service.START_NOT_STICKY;
    }*/
}
