package com.mobitv.ott.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mobitv.ott.R
import com.mobitv.ott.adapter.LiveTvPagerAdapter
import com.mobitv.ott.customview.TextViewBold
import com.mobitv.ott.customview.TextViewMedium
import com.mobitv.ott.model.LiveTvCategoryModel
import com.mobitv.ott.model.LiveTvChannelModel
import com.mobitv.ott.other.Const
import com.mobitv.ott.pojo.APIClient
import com.mobitv.ott.pojo.APIInterface
import com.mobitv.ott.pojo.ListChannelCategoryResponse
import kotlinx.android.synthetic.main.fragment_live_tv.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LiveTvFragment : Fragment(), ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener,
    View.OnClickListener {

    companion object {
        const val TAG = "live_tv"
        fun createInstance(): LiveTvFragment {
            return LiveTvFragment()
        }
    }

    private var vpAdapter: LiveTvPagerAdapter? = null
    private var apiInterface: APIInterface? = null
    val fragmentList = ArrayList<Fragment>()
    val titleList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_live_tv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vpAdapter =
            LiveTvPagerAdapter(activity!!.supportFragmentManager, fragmentList, titleList)
        vpLiveTvChannel.adapter = vpAdapter
        vpLiveTvChannel.addOnPageChangeListener(this)
        vpLiveTvChannel.currentItem = 0
        tlCategoryLiveTv.setupWithViewPager(vpLiveTvChannel, true)
        tlCategoryLiveTv.addOnTabSelectedListener(this)
        loadData()
    }

    private fun loadData() {
        apiInterface = APIClient.getInstance(activity!!).client.create(APIInterface::class.java)
        val call =
            apiInterface?.doGetLiveTvList()
        call?.enqueue(object : Callback<ListChannelCategoryResponse> {
            override fun onResponse(
                call: Call<ListChannelCategoryResponse>,
                response: Response<ListChannelCategoryResponse>
            ) {
                pbLoadingLiveTv?.visibility = View.GONE
                val listResponseObject = response.body()
                Log.e("Response", response.body().toString());
                if (listResponseObject?.statusCode == Const.STATUS_CODE_SUCCESS) {
                    var liveTvCategory: LiveTvCategoryModel
                    for (i in 0 until listResponseObject.listModel.size) {
                        liveTvCategory = listResponseObject.listModel[i]
                        titleList.add(liveTvCategory.name)
                        fragmentList.add(
                            LiveTvCategoryPageFragment.createInstance(
                                liveTvCategory.id, liveTvCategory.name, liveTvCategory.icon,
                                liveTvCategory.channelList as ArrayList<LiveTvChannelModel>
                            )
                        )
                    }
                    vpAdapter?.notifyDataSetChanged()
                    setTitlePage()
                }
            }
            override fun onFailure(call: Call<ListChannelCategoryResponse>, t: Throwable) {
                call.cancel()
                pbLoadingLiveTv?.visibility = View.GONE
            }
        })
    }


    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }


    override fun onPageScrollStateChanged(p0: Int) {
        //do nothing
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        //do nothing
    }

    override fun onPageSelected(pos: Int) {
        //setTitlePager()
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {
        //do nothing
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.customView = null
        tab?.customView = getTabView(activity, tab?.text.toString(), false)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.customView = null
        tab?.customView = getTabView(activity, tab?.text, true)
    }

    private fun setTitlePage() {
        val tabCount = if (tlCategoryLiveTv == null) {
            0
        } else {
            tlCategoryLiveTv.tabCount
        }
        for (i in 0 until tabCount) {
            val tab: TabLayout.Tab? = tlCategoryLiveTv?.getTabAt(i)
            tab?.customView = null
            if (i == vpLiveTvChannel.currentItem) {
                tab?.customView = getTabView(activity, titleList[i], true)
            } else {
                tab?.customView = getTabView(activity, titleList[i], false)
            }
        }
    }

    private fun getTabView(context: Context?, title: CharSequence?, isSelected: Boolean): TextView? {
        if (context != null) {
            val tv: TextView
            tv = TextViewBold(context)
            tv.setTextColor(ContextCompat.getColor(activity!!, R.color.colorTabUnSelected))
            tv.text = title
            tv.gravity = Gravity.CENTER
            tv.maxLines = 1
            val textSize = resources.getDimension(R.dimen.title_tab_size) / resources.displayMetrics.density
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            return tv
        }
        return null
    }
}