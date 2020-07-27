package com.mobitv.ott.fragment

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobitv.ott.R
import com.mobitv.ott.activity.MainActivity
import com.mobitv.ott.adapter.*
import com.mobitv.ott.database.entity.CommonEntity
import com.mobitv.ott.database.entity.SearchHistoryEntity
import com.mobitv.ott.dialog.LoadingDialog
import com.mobitv.ott.dialog.TwoOptionDialog
import com.mobitv.ott.model.SearchResultCategoryModel
import com.mobitv.ott.model.SearchSuggestionModel
import com.mobitv.ott.other.*
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.SearchResultRespone
import com.mobitv.ott.pojo.SearchSuggestionResponse
import com.mobitv.ott.task.*
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment(), View.OnClickListener, OnItemClickListener, KeyboardHeightObserver {
    companion object {
        const val TAG = "search"
        fun createInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    private var apiInterface: APIInterface? = null
    private var loadingDialog: LoadingDialog? = null
    private var listRecentSearch: ArrayList<SearchSuggestionModel> = ArrayList()
    private var listRecentSearchAdapter: ListStaticSearchRVAdapter? = null
    private var listTrendingSearch: ArrayList<SearchSuggestionModel> = ArrayList()
    private var listTrendingSearchAdapter: ListStaticSearchRVAdapter? = null
    private var listSuggestionSearch: ArrayList<SearchSuggestionModel> = ArrayList()
    private var listSuggestionSearchAdapter: ListDynamicSearchRVAdapter? = null

    private var pageLimit = 20
    private var alreadyListSize = 0
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var canLoadMore: Boolean = true
    private var keyboardHeightProvider: KeyboardHeightProvider? = null

    private var listSearchResultCategory: ArrayList<SearchResultCategoryModel> = ArrayList()
    private var listSearchResultCategoryRVAdapter: ListSearchResultCategoryRVAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCloseSearch.setOnClickListener(this)
        btnClearSearch.setOnClickListener(this)
        btnDeleteRecentSearch.setOnClickListener(this)
        edtSearch.addTextChangedListener(textChangedListener)

        loadingDialog = LoadingDialog(activity)
        //load recent search int database
        rvRecentSearch.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        listRecentSearchAdapter = ListStaticSearchRVAdapter("recent_search", activity!!, listRecentSearch)
        listRecentSearchAdapter?.setItemClickListener(this)
        rvRecentSearch.adapter = listRecentSearchAdapter
        //tim kiem gan day dc goi luon
        loadRecentSearch()

        //load trending search
        rvTrendingSearch.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        listTrendingSearchAdapter = ListStaticSearchRVAdapter("trending_search", activity!!, listTrendingSearch)
        listTrendingSearchAdapter?.setItemClickListener(this)
        rvTrendingSearch.adapter = listTrendingSearchAdapter

        //list suggestion search
        val suggestionRVLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rvSuggestSearch.layoutManager = suggestionRVLayoutManager
        listSuggestionSearchAdapter = ListDynamicSearchRVAdapter("suggestion_search", activity!!, listSuggestionSearch)
        listSuggestionSearchAdapter?.setItemClickListener(this)
        rvSuggestSearch.adapter = listSuggestionSearchAdapter
        scrollListener = object : EndlessRecyclerViewScrollListener(suggestionRVLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                alreadyListSize = listSuggestionSearch.size
                if (canLoadMore)
                    loadSuggestionSearch(edtSearch.text.toString().trim(), page)
            }
        }
        rvSuggestSearch.addOnScrollListener(scrollListener)

        //list search result
        rvSearchResult.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        listSearchResultCategoryRVAdapter =
            ListSearchResultCategoryRVAdapter(activity!!, listSearchResultCategory, rvSearchResult.width)
        listSearchResultCategoryRVAdapter?.setItemClickListener(itemSearchResultClickListener)
        rvSearchResult.adapter = listSearchResultCategoryRVAdapter
        btnCloseSearchResult.setOnClickListener(this)

        edtSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    doSearch(edtSearch.text.toString().trim())
                    AppUtils.hideSoftKeyboard(activity!!)
                    edtSearch.clearFocus()
                    return true
                }
                return false
            }
        })
        keyboardHeightProvider = KeyboardHeightProvider(activity)
        containerSearchPage.post {
            keyboardHeightProvider?.start()
        }
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val containerHeight = containerSearchPage.height
        val layoutParams = rvSuggestSearch.layoutParams as FrameLayout.LayoutParams
        if (height <= 0) {
            layoutParams.bottomMargin = 10
        } else {
            val offset = containerHeight - keyboardHeightProvider!!.screenHeight
            val delta = height + offset
            if (delta > 0) {
                layoutParams.bottomMargin = delta + 10
            }
        }
        rvSuggestSearch.layoutParams = layoutParams
    }

    private val textChangedListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!s.isNullOrEmpty()) {
                layoutSuggestionSearch.visibility = View.VISIBLE
                btnClearSearch.visibility = View.VISIBLE
                scrollListener.resetState()
                listSuggestionSearch.clear()
                alreadyListSize = 0
                loadSuggestionSearch(s.toString().trim(), 1)
            } else {
                layoutSuggestionSearch.visibility = View.INVISIBLE
                btnClearSearch.visibility = View.INVISIBLE
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    //hienvd: tim kiem de xuat cuar nguoi dung khi an chu tren giao dien
    private fun loadSuggestionSearch(keyword: String, page: Int) {
        pbLoadingSuggestionSearch?.visibility = View.VISIBLE
        apiInterface = APIClient.getInstance(activity!!).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetSuggestionSearch(keyword, page, pageLimit)
        call?.enqueue(object : Callback<SearchSuggestionResponse> {
            override fun onResponse(
                call: Call<SearchSuggestionResponse>,
                categoryResponse: Response<SearchSuggestionResponse>
            ) {
                pbLoadingSuggestionSearch?.visibility = View.GONE
                val response = categoryResponse.body()
                if (response!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    listSuggestionSearch.addAll(response.responseObject)
                    for (sModel in listSuggestionSearch) {
                        sModel.titleAlias = ConvertCharHelper.convertVN(sModel.title).toLowerCase() //temp
                        sModel.highlight = ConvertCharHelper.convertVN(keyword).toLowerCase()
                    }
                    listSuggestionSearchAdapter?.notifyDataSetChanged()
                    tvNoSearchSuggestion.visibility =
                        if (listSuggestionSearch.isEmpty()) View.VISIBLE else View.INVISIBLE
                    canLoadMore = response.responseObject.size >= pageLimit
                }
            }

            override fun onFailure(call: Call<SearchSuggestionResponse>, t: Throwable) {
                call.cancel()
                pbLoadingSuggestionSearch?.visibility = View.GONE
            }
        })
    }
    //hienvd: tim kiem gan day
    private fun loadRecentSearch() {
        Handler().postDelayed({
            loadingDialog?.showWindow()
            listRecentSearch.clear()
            val getSearchDataInDBTask = GetSearchDataInDBTask()
            getSearchDataInDBTask.listener = object : ExecuteTaskListResultListener {
                override fun onStartTask() {
                }

                override fun onResultSuccessfully(entities: MutableList<CommonEntity>?) {
                    loadingDialog?.closeWindow()
                    if (entities != null) {
                        var model: SearchSuggestionModel
                        for (entity in entities) {
                            entity as SearchHistoryEntity
                            model = SearchSuggestionModel()
                            model.title = (entity).keyword
                            model.titleAlias = (entity).keywordKhongDau
                            listRecentSearch.add(model)
                        }
                        listRecentSearchAdapter?.notifyDataSetChanged()
                        loadTrendingSearch()
                    }
                }

                override fun onResultFailed() {
                    loadingDialog?.closeWindow()
                    loadTrendingSearch()
                }

            }
            getSearchDataInDBTask.execute(activity!!)
        }, 300)
    }

    //hienvd: tim kiem theo xu huong
    private fun loadTrendingSearch() {
        loadingDialog?.showWindow()
        listTrendingSearch.clear()
        apiInterface = APIClient.getInstance(activity!!).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetTrendingSearch(1, 5)
        call?.enqueue(object : Callback<SearchSuggestionResponse> {
            override fun onResponse(
                call: Call<SearchSuggestionResponse>,
                categoryResponse: Response<SearchSuggestionResponse>
            ) {
                loadingDialog?.closeWindow()
                val response = categoryResponse.body()
                if (response!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    listTrendingSearch.addAll(response!!.responseObject)
                    listTrendingSearchAdapter?.notifyDataSetChanged()
                }
                AppUtils.showSoftKeyboard(activity!!, edtSearch)
            }

            override fun onFailure(call: Call<SearchSuggestionResponse>, t: Throwable) {
                call.cancel()
                loadingDialog?.closeWindow()
                AppUtils.showSoftKeyboard(activity!!, edtSearch)
            }
        })
    }

    override fun onItemClick(tag: String, position: Int) {
        when (tag) {
            "recent_search" -> {
                doSearch(listRecentSearch[position].title)
            }
            "trending_search" -> {
                doSearch(listTrendingSearch[position].title)
            }
            "suggestion_search" -> {
                doSearch(listSuggestionSearch[position].title)
            }
        }
    }

    override fun onItemLongClick(tag: String, position: Int) {
        //do nothing
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCloseSearch -> {
                (activity as MainActivity).doCloseSearchWindow()
            }
            R.id.btnClearSearch -> {
                edtSearch.setText("")
            }
            R.id.btnDeleteRecentSearch -> {
                val confirmDialog = TwoOptionDialog(activity)
                confirmDialog.setListener(object : TwoOptionDialog.OnButtonClickListener {
                    override fun onAgree() {
                        doDeleteSearchHistory()
                    }

                    override fun onCancel() {
                    }
                })
                confirmDialog.setMessageDialog("Xóa lịch sử tìm kiếm?")
                confirmDialog.showWindow(false)
            }
            R.id.btnCloseSearchResult -> {
                layoutSearchResult.visibility = View.GONE
                val outAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right)
                layoutSearchResult.startAnimation(outAnim)
                loadRecentSearch()
            }
        }
    }

    private val itemSearchResultClickListener = object : ListSearchResultRVAdapter.OnItemClickListener {
        override fun onItemClick(tag: String, parentPos: Int, position: Int) {
            (activity as MainActivity).goResult(listSearchResultCategory[parentPos].list[position])
        }

        override fun onItemLongClick(tag: String, parentPos: Int, position: Int) {
            //do nothing
        }

    }
    //danh sach ket qua
    private fun loadSearch(keyword: String) {
        loadingDialog?.showWindow()
        listSearchResultCategory.clear()
        apiInterface = APIClient.getInstance(activity!!).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetSearchResult(keyword, 1, 20)
        call?.enqueue(object : Callback<SearchResultRespone> {
            override fun onResponse(
                call: Call<SearchResultRespone>,
                categoryResponse: Response<SearchResultRespone>
            ) {
                loadingDialog?.closeWindow()
                val response = categoryResponse.body()
                if (response!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    listSearchResultCategory.addAll(response!!.responseObject)
                    listSearchResultCategoryRVAdapter?.notifyDataSetChanged()
                    tvNoSearchResult.visibility =
                        if (listSearchResultCategory.isEmpty()) View.VISIBLE else View.INVISIBLE
                }
            }

            override fun onFailure(call: Call<SearchResultRespone>, t: Throwable) {
                call.cancel()
                loadingDialog?.closeWindow()
            }
        })
    }
    //khi lua chon vao danh sach da tim kiem
    private fun doSearch(keyword: String) {
        updateSearchHistory(keyword)
        layoutSearchResult.visibility = View.VISIBLE
        val inAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right)
        inAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                //do nothing
            }

            override fun onAnimationEnd(animation: Animation?) {
                Handler().post {
                    listSearchResultCategoryRVAdapter?.setContainerWidth(rvSearchResult.width)
                }
                tvTitleSearchResult.text = keyword
                loadSearch(keyword)
            }

            override fun onAnimationStart(animation: Animation?) {
                //do nothing
            }

        })
        layoutSearchResult.startAnimation(inAnim)

    }

    private fun updateSearchHistory(keyword: String) {
        val userID = 0
        val currentTime = System.currentTimeMillis()
        val insertOrUpdateSearchDBTask =
            InsertOrUpdateSearchDBTask(userID, keyword, ConvertCharHelper.convertVN(keyword).toLowerCase(), currentTime)
        insertOrUpdateSearchDBTask.listener = object : ExecuteTaskSingleResultListener {
            override fun onStartTask() {
                //do nothing
            }

            override fun onResultSuccessfully(entity: CommonEntity?) {
                entity as SearchHistoryEntity
                Log.d(TAG, "SUCCESS: ${entity.keyword}")
            }

            override fun onResultFailed() {
                Log.d(TAG, "FAILED")
            }

        }
        insertOrUpdateSearchDBTask.execute(activity)
    }

    private fun doDeleteSearchHistory() {
        loadingDialog?.showWindow()
        val deleteAllSearchHistoryDBTask = DeleteAllSearchHistoryDBTask()
        deleteAllSearchHistoryDBTask.listener = object : ExecuteTaskSingleResultListener {
            override fun onStartTask() {
                //do nothing
            }

            override fun onResultSuccessfully(entity: CommonEntity?) {
                loadingDialog?.closeWindow()
                loadRecentSearch()
                Log.d(TAG, "DELETE SUCCESS")
            }

            override fun onResultFailed() {
                loadingDialog?.closeWindow()
                Log.d(TAG, "DELETE FAILED")
            }

        }
        deleteAllSearchHistoryDBTask.execute(activity)
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightProvider?.setKeyboardHeightObserver(this)
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider?.setKeyboardHeightObserver(null)
    }

    override fun onDestroy() {
        keyboardHeightProvider?.close()
        super.onDestroy()
    }
}