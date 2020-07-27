package com.mobitv.ott.task;

import androidx.annotation.Nullable;
import com.mobitv.ott.database.entity.CommonEntity;
import com.mobitv.ott.database.entity.WatchHistoryEntity;

/**
 * Created by sonth on 2/28/2017.
 */

public interface ExecuteTaskSingleResultListener {
    void onStartTask();
    void onResultSuccessfully(@Nullable CommonEntity entity);
    void onResultFailed();
}
