package com.mobitv.ott.task

import android.content.Context
import android.os.AsyncTask
import com.mobitv.ott.database.DatabaseClient
import com.mobitv.ott.database.entity.WatchHistoryEntity

class GetWatchDataInDBTask() : AsyncTask<Context, Void, Void>() {
    var listener: ExecuteTaskSingleResultListener? = null
    private var result: Boolean = false
    private var watchHistoryEntity : WatchHistoryEntity? = null
    private var videoID = 0
    private var videoType = ""


    constructor(videoID : Int, videoType : String) : this() {
        this.videoID = videoID
        this.videoType = videoType
    }

    override fun onPreExecute() {
        super.onPreExecute()
        listener?.onStartTask()
    }

    override fun doInBackground(vararg a: Context): Void? {
        watchHistoryEntity = DatabaseClient.getInstance(a[0])
            .appDatabase.watchHistoryDao().loadWatchHistoryByVideo(videoID, videoType)
        result = true
        return null
    }

    override fun onPostExecute(p: Void?) {
        super.onPostExecute(p)
        if (result) {
            listener?.onResultSuccessfully(watchHistoryEntity)
        } else {
            listener?.onResultFailed()
        }
    }
}