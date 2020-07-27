package com.mobitv.ott.activity

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.SparseBooleanArray
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.mobitv.ott.R
import com.mobitv.ott.adapter.ListCommentRVAdapter
import com.mobitv.ott.adapter.OnItemClickListener
import com.mobitv.ott.dialog.LoadingDialog
import com.mobitv.ott.dialog.NotificationDialog
import com.mobitv.ott.model.CommentModel
import com.mobitv.ott.other.*
import com.mobitv.ott.pojo.*
import kotlinx.android.synthetic.main.activity_vod_comment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VodCommentActivity : BaseActivity(), OnItemClickListener, KeyboardHeightObserver, TextWatcher {
    private val listComment: ArrayList<CommentModel> = ArrayList()
    private var listCommentRVAdapter: ListCommentRVAdapter? = null
    private var apiInterface: APIInterface? = null
    private var pageLimit = 20
    private var alreadyListSize = 0
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var canLoadMore: Boolean = true
    private var keyboardHeightProvider: KeyboardHeightProvider? = null
    private var originYContent = 0f
    private var vodID = 0
    private var loadingDialog: LoadingDialog? = null
    private var allCommentCount = 0
    private lateinit var myLayoutManager: LinearLayoutManager
    private var sparseBooleanArray : SparseBooleanArray = SparseBooleanArray()
    private var startHeightComment = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setContentView(R.layout.activity_vod_comment)
        btnBackVodComment.setOnClickListener(this)
        edtComment.addTextChangedListener(this)
        btnSendComment.setOnClickListener(this)

        keyboardHeightProvider = KeyboardHeightProvider(this)
        layoutComment.post {
            originYContent = layoutComment.y
            startHeightComment = layoutComment.height
            keyboardHeightProvider?.start()
        }
        loadingDialog = LoadingDialog(this)
        val iconURL = intent?.getStringExtra(Const.ICON)
        Glide.with(this)
            .load(iconURL)
            .apply(RequestOptions().signature(ObjectKey(GlobalParams.VERSION_CODE)))
            .into(imvVodAvatar)
        vodID = intent.getIntExtra(Const.ID, 0)
        myLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvListComments.layoutManager = myLayoutManager
        rvListComments.setHasFixedSize(true)
        listCommentRVAdapter = ListCommentRVAdapter(this, listComment, sparseBooleanArray)
        listCommentRVAdapter?.setItemClickListener(this)
        rvListComments.adapter = listCommentRVAdapter
        scrollListener = object : EndlessRecyclerViewScrollListener(myLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                alreadyListSize = listComment.size
                if (canLoadMore)
                    loadData(vodID, page)
            }
        }
        rvListComments.addOnScrollListener(scrollListener)
        loadData(vodID, 1)
    }

    override fun afterTextChanged(s: Editable?) {
        btnSendComment.isEnabled = !s?.trim().isNullOrEmpty()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val containerHeight = containerCommentActivity.height
        val deltaHeightComment = layoutComment.height - startHeightComment
        if (height <= 0) {
            layoutComment.animate().y(originYContent - deltaHeightComment)
        } else {
            val offset = containerHeight - keyboardHeightProvider!!.screenHeight
            val delta = height + offset
            if (delta > 0)
                layoutComment.animate().y(originYContent - deltaHeightComment - delta)
        }
    }

    private fun loadData(id: Int, page: Int) {
        loadingDialog?.showWindow()
        apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        val call = apiInterface?.doGetAllComment(
            id, page, pageLimit
        )
        call?.enqueue(object : Callback<CommentResponse> {
            override fun onResponse(
                call: Call<CommentResponse>,
                categoryResponse: Response<CommentResponse>
            ) {
                loadingDialog?.closeWindow()
                val responseObject = categoryResponse.body()
                if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    allCommentCount = responseObject!!.responseObject.count
                    val allComment = responseObject!!.responseObject.list
                    if (allComment.size > 0) {
                        for (i in 0 until allComment.size) {
                            allComment[i].level = 0
                            checkExistAndAddComment(allComment[i])
                            if (!allComment[i].root.isEmpty()) {
                                for (model in allComment[i].root) {
                                    model.level = 1
                                }
                                listComment.addAll(i + 1, allComment[i].root)
                            }
                        }
                        listCommentRVAdapter?.notifyItemRangeInserted(alreadyListSize, listComment.size - 1)
                        val amountComment = getString(R.string.comment) + " (" + allCommentCount + ")"
                        tvCommentTitle.text = amountComment
                        canLoadMore = allComment.size >= pageLimit
                    }
                }
                if (!listComment.isEmpty()) {
                    tvNoComment.text = ""
                } else {
                    tvNoComment.text = getString(R.string.no_comment_yet)
                }
            }

            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                call.cancel()
                loadingDialog?.closeWindow()
            }
        })
    }

    private fun sendComment() {
        loadingDialog?.showWindow()
        apiInterface = APIClient.getInstance(this).client.create(APIInterface::class.java)
        val call = apiInterface?.doUpComment(
            CommentParams(vodID.toString(), null, edtComment.text.toString().trim())
        )
        call?.enqueue(object : Callback<SendCommentResponse> {
            override fun onResponse(
                call: Call<SendCommentResponse>,
                categoryResponse: Response<SendCommentResponse>
            ) {
                loadingDialog?.closeWindow()
                val responseObject = categoryResponse.body()
                if (responseObject!!.statusCode == Const.STATUS_CODE_SUCCESS) {
                    listComment.add(0, responseObject!!.responseObject)
                }
                sparseBooleanArray.clear()
                allCommentCount++
                val amountComment = getString(R.string.comment) + " (" + (allCommentCount) + ")"
                tvCommentTitle.text = amountComment
                if (!listComment.isEmpty()) {
                    tvNoComment.text = ""
                } else {
                    tvNoComment.text = getString(R.string.no_comment_yet)
                }
                listCommentRVAdapter?.notifyDataSetChanged()
                edtComment.setText("")
                AppUtils.hideSoftKeyboard(this@VodCommentActivity)
                edtComment.clearFocus()
                Handler().postDelayed({
                    myLayoutManager.scrollToPositionWithOffset(0, 0)
                }, 200)
            }

            override fun onFailure(call: Call<SendCommentResponse>, t: Throwable) {
                call.cancel()
                loadingDialog?.closeWindow()
            }
        })
    }

    override fun onItemClick(tag: String, position: Int) {
        if (position < listComment.size) {

        }
    }

    override fun onItemLongClick(tag: String, position: Int) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBackVodComment -> {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
            R.id.btnSendComment -> {
                if (MyPreferenceManager.getInstance(this).isLogIn) {
                    sendComment()
                } else {
                    val notificationDialog = NotificationDialog(this)
                    notificationDialog.setMessageDialog(getString(R.string.request_login_message))
                    notificationDialog.showWindow(false)
                }
            }
        }
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

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun checkExistAndAddComment(newItem: CommentModel) {
        var isExist = false
        for (model in listComment) {
            isExist = isExist || (model.id == newItem.id)
        }
        if (!isExist) {
            listComment.add(newItem)
        }
    }
}
