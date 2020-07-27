package com.mobitv.ott.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobitv.ott.R
import com.mobitv.ott.adapter.ListVodRVAdapter
import com.mobitv.ott.adapter.OnItemClickListener
import com.mobitv.ott.dialog.LoadingDialog
import com.mobitv.ott.model.VodModel
import com.mobitv.ott.other.AppUtils
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.EndlessRecyclerViewScrollListener
import com.mobitv.ott.other.GridItemVodInsetDecoration
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.VodResponse
import kotlinx.android.synthetic.main.activity_list_vod.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListVodActivity : BaseActivity(), OnItemClickListener {

    private val listVod: ArrayList<VodModel> = ArrayList()
    private var listVodRVAdapter: ListVodRVAdapter? = null
    private var apiInterface: APIInterface? = null
    private var alreadyListSize = 0
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var canLoadMore: Boolean = true
    private lateinit var type: String
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_list_vod)
        btnBackCategoryDetails.setOnClickListener(this)
        loadingDialog = LoadingDialog(this)
        var catName = intent?.getStringExtra(Const.NAME)
        val id = intent?.getStringExtra(Const.ID)
        if (intent != null)
            type = intent!!.getStringExtra(Const.TYPE)!!
        catName =  catName
        tvTitleListVod.text = catName
//        val column = 2
        //vtv cab
        val column = 2
        val myLayoutManager = GridLayoutManager(this, column)
        rvVod.layoutManager = myLayoutManager
        rvVod.setHasFixedSize(true)
        rvVod.addItemDecoration(GridItemVodInsetDecoration(this, column))
        listVodRVAdapter = ListVodRVAdapter(this, listVod, true)
        listVodRVAdapter?.setItemClickListener(this)
        rvVod.adapter = listVodRVAdapter
        scrollListener = object : EndlessRecyclerViewScrollListener(myLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                alreadyListSize = listVod.size
                if (canLoadMore)
                    loadData(id, page)
            }
        }
        rvVod.addOnScrollListener(scrollListener)
        loadData(id, 1)
    }

    private fun loadData(id: String?, page: Int) {
        if (id != null) {
            loadingDialog?.showWindow()
            apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
            val call = apiInterface?.doGetListVodByCategory(
                id, page, 10
            )
            call?.enqueue(object : Callback<VodResponse> {
                override fun onResponse(
                    call: Call<VodResponse>,
                    categoryResponse: Response<VodResponse>
                ) {
                    loadingDialog?.closeWindow()
                    val listResponseObject = categoryResponse.body()
                    if (listResponseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                        if (listResponseObject.listVodModel.size > 0) {
                            for (i in 0 until listResponseObject.listVodModel.size) {
                                listVod.add(listResponseObject.listVodModel[i])
                            }
                            listVodRVAdapter?.notifyItemRangeInserted(alreadyListSize, listVod.size - 1)
                            canLoadMore = true
                        } else {
                            canLoadMore = false
                        }
                    }
                }

                override fun onFailure(call: Call<VodResponse>, t: Throwable) {
                    call.cancel()
                    loadingDialog?.closeWindow()
                }
            })
        }
    }

    override fun onItemClick(tag: String, position: Int) {
        if (position < listVod.size) {
            goVodDetails(listVod[position].id)
        }
    }

    override fun onItemLongClick(tag: String, position: Int) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBackCategoryDetails -> {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }

    private fun goVodDetails(id: Int) {
        AppUtils.goVodDetails(this, id, type)
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
