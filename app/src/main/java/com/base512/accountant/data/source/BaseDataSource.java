package com.base512.accountant.data.source;

import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.Task;

import java.util.LinkedHashMap;

/**
 * Created by Thomas on 9/9/2016.
 */
public interface BaseDataSource {
    interface LoadDataCallback<T extends DataObject> extends AccessDataCallback {
        void onDataLoaded(LinkedHashMap<String, T> data);
    }

    interface GetDataCallback<T extends DataObject> extends AccessDataCallback {
        void onDataLoaded(T data);
    }

    interface UpdateDataCallback extends AccessDataCallback {
        void onDataUpdated(String id);
    }
}
