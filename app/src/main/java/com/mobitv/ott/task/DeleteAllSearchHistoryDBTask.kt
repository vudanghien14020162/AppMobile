package com.mobitv.ott.task

import android.content.Context
import android.os.AsyncTask
import com.mobitv.ott.database.DatabaseClient

class DeleteAllSearchHistoryDBTask() : AsyncTask<Context, Void, Void>() {
    var listener: ExecuteTaskSingleResultListener? = null
    private var result: Boolean = false

    override fun onPreExecute() {
        super.onPreExecute()
        listener?.onStartTask()
    }

    override fun doInBackground(vararg a: Context): Void? {
        DatabaseClient.getInstance(a[0])
            .appDatabase.searchHistoryDao().deleteAll()
        result = true
        return null
    }

    override fun onPostExecute(p: Void?) {
        super.onPostExecute(p)
        if (result) {
            listener?.onResultSuccessfully(null)
        } else {
            listener?.onResultFailed()
        }
    }
}