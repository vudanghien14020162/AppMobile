package com.mobitv.ott.task;

import androidx.annotation.Nullable;
import com.mobitv.ott.database.entity.CommonEntity;

import java.util.List;

/**
 * Created by sonth on 2/28/2017.
 */

public interface ExecuteTaskListResultListener {
    void onStartTask();
    void onResultSuccessfully(@Nullable List<CommonEntity> entities);
    void onResultFailed();
}
