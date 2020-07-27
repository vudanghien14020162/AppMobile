package com.mobitv.ott.task

import android.content.Context
import android.os.AsyncTask
import com.mobitv.ott.database.DatabaseClient
import com.mobitv.ott.database.entity.WatchHistoryEntity

class GetCurentPositionDBTask() : AsyncTask<Context, Void, Void>() {
    var listener: ExecuteTaskSingleResultListener? = null
    private var result: Boolean = false
    private var watchHistoryEntity : WatchHistoryEntity? = null
    private var videoID = 0
    private var episodeID = 0
    private var videoType = ""


    constructor(videoID : Int, videoType : String, episodeID : Int) : this() {
        this.videoID = videoID
        this.videoType = videoType
        this.episodeID = episodeID
    }

    override fun onPreExecute() {
        super.onPreExecute()
        listener?.onStartTask()
    }

    override fun doInBackground(vararg a: Context): Void? {
        watchHistoryEntity = DatabaseClient.getInstance(a[0])
            .appDatabase.watchHistoryDao().getCurrentPosition(videoID, videoType, episodeID)
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