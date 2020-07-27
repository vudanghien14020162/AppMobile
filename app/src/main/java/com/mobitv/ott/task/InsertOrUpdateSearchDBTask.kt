package com.mobitv.ott.task

import android.content.Context
import android.os.AsyncTask
import com.mobitv.ott.database.DatabaseClient
import com.mobitv.ott.database.entity.SearchHistoryEntity

class InsertOrUpdateSearchDBTask() : AsyncTask<Context, Void, Void>() {
    var listener: ExecuteTaskSingleResultListener? = null
    private var result: Boolean = false
    private var entity : SearchHistoryEntity? = null
    private var userID = 0
    private var keyword = ""
    private var keywordKhongDau = ""
    private var searchDate = 0L

    constructor(userID : Int, keyword : String, keywordKhongDau : String, searchDate: Long) : this() {
        this.userID = userID
        this.keyword = keyword
        this.keywordKhongDau = keywordKhongDau
        this.searchDate = searchDate
    }

    override fun onPreExecute() {
        super.onPreExecute()
        listener?.onStartTask()
    }

    override fun doInBackground(vararg a: Context): Void? {
        entity = DatabaseClient.getInstance(a[0]).appDatabase.searchHistoryDao().loadSearchByKeyword(keywordKhongDau)
        if(entity == null) {
            entity = SearchHistoryEntity()
            entity!!.userID = userID
            entity!!.keyword = keyword
            entity!!.keywordKhongDau = keywordKhongDau
            entity!!.searchDate = searchDate
            DatabaseClient.getInstance(a[0]).appDatabase.searchHistoryDao().insert(entity)
        }else{
            entity!!.searchDate = searchDate
            DatabaseClient.getInstance(a[0]).appDatabase.searchHistoryDao().update(entity)
        }
        result = true
        return null
    }

    override fun onPostExecute(p: Void?) {
        super.onPostExecute(p)
        if (result) {
            listener?.onResultSuccessfully(entity)
        } else {
            listener?.onResultFailed()
        }
    }
}