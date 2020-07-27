package com.mobitv.ott.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.mobitv.ott.R
import com.mobitv.ott.activity.SignInActivity
import com.mobitv.ott.adapter.ListVodRVAdapter
import com.mobitv.ott.adapter.OnItemClickListener
import com.mobitv.ott.model.VodModel
import com.mobitv.ott.other.AppUtils
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.GridItemPersonalInsetDecoration
import com.mobitv.ott.other.MyPreferenceManager
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.VodCategoryResponse
import kotlinx.android.synthetic.main.fragment_page_personal.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalFavouriteFragment : CommonPageFragment(), OnItemClickListener {

    companion object {
        const val TAG = "personal_favourite"
        fun createInstance(): PersonalFavouriteFragment {
            return PersonalFavouriteFragment()
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
        if(MyPreferenceManager.getInstance(activity).isLogIn){
            tvNoItems.text = getString(R.string.no_favourite_item)
        }else{
            //set up term of use clickable
            val ss = SpannableString(getString(R.string.request_login_message))
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    goSignIn()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    if (tvNoItems.isPressed) {
                        ds.color = resources.getColor(R.color.colorAlmostTransparent)
                    } else {
                        ds.color = Color.parseColor("#e64354")
                    }
                    ds.isUnderlineText = false
                    tvNoItems.invalidate()
                }
            }
            ss.setSpan(clickableSpan, 8, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvNoItems.highlightColor = Color.TRANSPARENT
            tvNoItems.text = ss
            tvNoItems.movementMethod = LinkMovementMethod.getInstance()
        }
        loadData()
    }

    private fun goSignIn(){
        val intent = Intent(activity, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun reloadData() {
        loadData()
    }

    private fun loadData() {
        pbLoadingPagePersonal?.visibility = View.VISIBLE
        vodList.clear()
        apiInterface = APIClient.getInstance(activity!!).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetListVodFavourite()
        call?.enqueue(object : Callback<VodCategoryResponse> {
            override fun onResponse(
                call: Call<VodCategoryResponse>,
                categoryResponse: Response<VodCategoryResponse>
            ) {
                pbLoadingPagePersonal?.visibility = View.INVISIBLE
                val listResponseObject = categoryResponse.body()
                if (listResponseObject != null && listResponseObject.statusCode == Const.STATUS_CODE_SUCCESS) {
                    if (listResponseObject.listVodCategoryModel.size > 0) {
                        for (film in listResponseObject.listVodCategoryModel[0].listFilm) {
                            film.iconUrl = listResponseObject.listVodCategoryModel[0].baseURL + film.iconUrl
                            vodList.add(film)
                        }
                    }
                    listVodRVAdapter?.notifyDataSetChanged()
                    if (!vodList.isEmpty()) {
                        tvNoItems?.text = ""
                    } else {
                        tvNoItems?.text = getString(R.string.no_favourite_item)
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