package com.mobitv.ott.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobitv.ott.R
import com.mobitv.ott.activity.ListVodActivity
import com.mobitv.ott.activity.VodDetailsActivity
import com.mobitv.ott.adapter.ListTvShowsCategoryRVAdapter
import com.mobitv.ott.adapter.OnItemNestedListClickListener
import com.mobitv.ott.model.VodCategoryModel
import com.mobitv.ott.other.AppUtils
import com.mobitv.ott.other.Const
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.VodCategoryResponse
import kotlinx.android.synthetic.main.fragment_tv_shows.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.mobitv.ott.fragment.TvShowsFragment
class TvShowsFragment : Fragment(), OnItemNestedListClickListener {

    companion object {
        const val TAG = "tv_shows"
        fun createInstance(): TvShowsFragment {
            return TvShowsFragment()
        }
    }

    private var tvShowsCategoryRVAdapter: ListTvShowsCategoryRVAdapter? = null
    private val listVodCategoryModel: ArrayList<VodCategoryModel> = ArrayList()
    private var apiInterface: APIInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tv_shows, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvTvShows.layoutManager = LinearLayoutManager(
            activity!!,
            RecyclerView.VERTICAL,
            false
        )
        rvTvShows.setHasFixedSize(true)
        tvShowsCategoryRVAdapter =
            ListTvShowsCategoryRVAdapter(activity!!, listVodCategoryModel, resources.displayMetrics.widthPixels)
        tvShowsCategoryRVAdapter?.setItemClickListener(this)
        rvTvShows.adapter = tvShowsCategoryRVAdapter
        loadData()
    }

    private fun goCategoryDetails(id: String, name: String) {
        val intent = Intent(activity!!, ListVodActivity::class.java)
        intent.putExtra(Const.ID, id)
        intent.putExtra(Const.NAME,  name)
        intent.putExtra(Const.TYPE, Const.TYPE_TV_SHOWS_SEASON)
        startActivity(intent)
        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun goTvShowsDetails(id: Int) {
        AppUtils.goVodDetails(activity!!, id, Const.TYPE_TV_SHOWS_SEASON)
    }

    override fun onHeaderClick(tag: String, position: Int, id: String) {
        goCategoryDetails(id, listVodCategoryModel[position].name)
    }

    override fun onItemClick(tag: String, position: Int, id: Int) {
        goTvShowsDetails(id)
    }

    override fun onItemLongClick(tag: String, position: Int, id: Int) {
    }

    override fun onMoreClick(tag: String, catID: String, categoryName: String) {
        goCategoryDetails(catID, categoryName)
    }

    private fun loadData() {
        apiInterface = APIClient.getInstance(activity!!).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetTvShowsList()
        call?.enqueue(object : Callback<VodCategoryResponse> {
            override fun onResponse(
                call: Call<VodCategoryResponse>,
                categoryResponse: Response<VodCategoryResponse>
            ) {
                pbLoadingTvShows?.visibility = View.GONE
                val listResponseObject = categoryResponse.body()
                if (listResponseObject?.statusCode == Const.STATUS_CODE_SUCCESS) {
                    for (i in 0 until listResponseObject.listVodCategoryModel.size) {
                        listVodCategoryModel.add(listResponseObject.listVodCategoryModel[i])
                    }
                    tvShowsCategoryRVAdapter?.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<VodCategoryResponse>, t: Throwable) {
                call.cancel()
                pbLoadingTvShows?.visibility = View.GONE
            }
        })
    }
}