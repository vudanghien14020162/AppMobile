package com.mobitv.ott.task

import android.content.Context
import android.os.AsyncTask
import com.mobitv.ott.database.DatabaseClient
import com.mobitv.ott.database.entity.WatchHistoryEntity

class GetAllWatchHistoryInDBTask() : AsyncTask<Context, Void, Void>() {
    var listener: ExecuteTaskListResultListener? = null
    private var result: Boolean = false
    private var entities: List<WatchHistoryEntity>? = null

    override fun onPreExecute() {
        super.onPreExecute()
        listener?.onStartTask()
    }

    override fun doInBackground(vararg a: Context): Void? {
        entities = DatabaseClient.getInstance(a[0])
            .appDatabase.watchHistoryDao().loadAllWatchHistory()
        result = true
        return null
    }

    override fun onPostExecute(p: Void?) {
        super.onPostExecute(p)
        if (result) {
            listener?.onResultSuccessfully(entities)
        } else {
            listener?.onResultFailed()
        }
    }
}