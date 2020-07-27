package com.mobitv.ott.task

import android.content.Context
import android.os.AsyncTask
import com.mobitv.ott.database.DatabaseClient
import com.mobitv.ott.database.entity.WatchHistoryEntity

class InsertOrUpdateWatchDBTask() : AsyncTask<Context, Void, Void>() {
    var listener: ExecuteTaskSingleResultListener? = null
    private var result: Boolean = false
    private var watchHistoryEntity : WatchHistoryEntity? = null
    private var userID = 0
    private var videoID = 0
    private var videoType = ""
    private var episodeID = 0
    private var currentPosition = 0L
    private var watchDate = 0L

    constructor(userID : Int, videoID : Int, videoType : String, episodeID : Int, currentPosition : Long, watchDate: Long) : this() {
        this.userID = userID
        this.videoID = videoID
        this.videoType = videoType
        this.episodeID = episodeID
        this.currentPosition = currentPosition
        this.watchDate = watchDate
    }

    override fun onPreExecute() {
        super.onPreExecute()
        listener?.onStartTask()
    }

    override fun doInBackground(vararg a: Context): Void? {
        watchHistoryEntity = DatabaseClient.getInstance(a[0]).appDatabase.watchHistoryDao().loadWatchHistoryByVideo(videoID, videoType)
        if(watchHistoryEntity == null) {
            watchHistoryEntity = WatchHistoryEntity()
            watchHistoryEntity!!.userID = userID
            watchHistoryEntity!!.itemID = videoID
            watchHistoryEntity!!.itemType = videoType
            watchHistoryEntity!!.episodeID = episodeID
            watchHistoryEntity!!.currentPosition = currentPosition
            watchHistoryEntity!!.watchDate = watchDate
            DatabaseClient.getInstance(a[0]).appDatabase.watchHistoryDao().insert(watchHistoryEntity)
        }else{
            watchHistoryEntity!!.userID = userID
            watchHistoryEntity!!.itemID = videoID
            watchHistoryEntity!!.itemType = videoType
            watchHistoryEntity!!.episodeID = episodeID
            watchHistoryEntity!!.currentPosition = currentPosition
            watchHistoryEntity!!.watchDate = watchDate
            DatabaseClient.getInstance(a[0]).appDatabase.watchHistoryDao().update(watchHistoryEntity)
        }
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