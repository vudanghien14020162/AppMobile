package com.mobitv.ott.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.mobitv.ott.R
import com.mobitv.ott.adapter.ListVodRVAdapter
import com.mobitv.ott.adapter.OnItemClickListener
import com.mobitv.ott.database.entity.CommonEntity
import com.mobitv.ott.database.entity.WatchHistoryEntity
import com.mobitv.ott.model.VodModel
import com.mobitv.ott.other.AppUtils
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.GridItemPersonalInsetDecoration
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.ListVodIdParams
import com.mobitv.ott.pojo.VodCategoryResponse
import com.mobitv.ott.task.ExecuteTaskListResultListener
import com.mobitv.ott.task.GetAllWatchHistoryInDBTask
import kotlinx.android.synthetic.main.fragment_page_personal.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalWatchHistoryFragment : CommonPageFragment(), OnItemClickListener {

    companion object {
        const val TAG = "personal_watch_history"
        fun createInstance(): PersonalWatchHistoryFragment {
            return PersonalWatchHistoryFragment()
        }
    }

    private var listVodRVAdapter: ListVodRVAdapter? = null
    private var vodList = ArrayList<VodModel>()
    private var apiInterface: APIInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val column = 4
//        vtv cab
        val column = 2
        listVodRVAdapter = ListVodRVAdapter(activity!!, vodList, false)
        listVodRVAdapter?.setItemClickListener(this)
        rvVodPersonal.layoutManager = GridLayoutManager(activity!!, column)
        rvVodPersonal.addItemDecoration(GridItemPersonalInsetDecoration(activity!!, column))
        rvVodPersonal.adapter = listVodRVAdapter
        tvNoItems.text = getString(R.string.no_watched_item)
        loadDataInDB()
    }

    override fun reloadData(){
        loadDataInDB()
    }

    private fun loadDataInDB() {
        pbLoadingPagePersonal?.visibility = View.VISIBLE
        vodList.clear()
        val getWatchHistoryDBTask = GetAllWatchHistoryInDBTask()
        getWatchHistoryDBTask.listener = object : ExecuteTaskListResultListener {
            override fun onStartTask() {
            }

            override fun onResultSuccessfully(entities: MutableList<CommonEntity>?) {
                if (entities != null) {
                    val listID = ArrayList<Int>()
                    for (entity in entities) {
                        entity as WatchHistoryEntity
                        listID.add(entity.itemID)
                    }
                    loadVodByListID(listID)
                }
            }

            override fun onResultFailed() {
                pbLoadingPagePersonal?.visibility = View.INVISIBLE
            }
        }
        getWatchHistoryDBTask.execute(activity)
    }

    private fun loadVodByListID(listID: List<Int>) {
        apiInterface = APIClient.getInstance(activity!!).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetListVodWatched(ListVodIdParams(listID))
        call?.enqueue(object : Callback<VodCategoryResponse> {
            override fun onResponse(
                call: Call<VodCategoryResponse>,
                categoryResponse: Response<VodCategoryResponse>
            ) {
                pbLoadingPagePersonal?.visibility = View.INVISIBLE
                val listResponseObject = categoryResponse.body()
                if (listResponseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    if (listResponseObject!!.listVodCategoryModel.size > 0) {
                        for(film in listResponseObject!!.listVodCategoryModel[0].listFilm){
                            film.iconUrl = listResponseObject!!.listVodCategoryModel[0].baseURL + film.iconUrl
                            vodList.add(film)
                        }
                    }
                    listVodRVAdapter?.notifyDataSetChanged()
                    if(!vodList.isEmpty()){
                        tvNoItems.text = ""
                    }else{
                        tvNoItems.text = getString(R.string.no_watched_item)
                    }
                }
            }

            override fun onFailure(call: Call<VodCategoryResponse>, t: Throwable) {
                call.cancel()
                pbLoadingPagePersonal?.visibility = View.INVISIBLE
            }
        })
    }

    override fun onItemClick(tag: String, position: Int) {
        AppUtils.goVodDetails(activity!!, vodList[position].id, vodList[position].vodType)
    }

    override fun onItemLongClick(tag: String, position: Int) {
        //do nothing
    }

}