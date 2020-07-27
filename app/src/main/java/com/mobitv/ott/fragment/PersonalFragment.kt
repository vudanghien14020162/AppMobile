package com.mobitv.ott.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.mobitv.ott.R
import com.mobitv.ott.adapter.PersonalPagerAdapter
import com.mobitv.ott.customview.TextViewBold
import com.mobitv.ott.customview.TextViewLight
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.GlobalParams
import com.mobitv.ott.other.MyPreferenceManager
import kotlinx.android.synthetic.main.fragment_personal.*

class PersonalFragment : Fragment(), TabLayout.OnTabSelectedListener {

    companion object {
        const val TAG = "personal"
        fun createInstance(): PersonalFragment {
            return PersonalFragment()
        }
    }

    private val pageTitle = arrayListOf("Yêu thích", "Đã xem", "Đã tải")
    private var vpAdapter: PersonalPagerAdapter? = null
    private val fragmentList = ArrayList<CommonPageFragment>()
    private val titleList = ArrayList<String>()
    private var isPaused = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (MyPreferenceManager.getInstance(activity!!).isLogIn) {
            val phoneNumber =
                MyPreferenceManager.getInstance(activity!!).getValue(Const.USER_NAME, getString(R.string.app_name))
            val part1 = phoneNumber.substring(0, 3)
            val part2 = phoneNumber.substring(7, phoneNumber.length)
            val clearPhoneNumber = "$part1****$part2"
            tvUserName.text = clearPhoneNumber
        }
        vpAdapter =
            PersonalPagerAdapter(activity!!.supportFragmentManager, fragmentList, titleList)
        vpPersonal.adapter = vpAdapter
        vpPersonal.currentItem = 0
        tlTabPersonal.setupWithViewPager(vpPersonal, true)
        tlTabPersonal.addOnTabSelectedListener(this)
        loadData()
    }

    private fun loadData() {
        titleList.addAll(pageTitle)
        fragmentList.add(PersonalFavouriteFragment.createInstance())
        fragmentList.add(PersonalWatchHistoryFragment.createInstance())
        fragmentList.add(PersonalDownloadedFragment.createInstance())
        vpAdapter?.notifyDataSetChanged()
        setTitlePage()
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
        val tabCount = if (tlTabPersonal == null) {
            0
        } else {
            tlTabPersonal.tabCount
        }
        for (i in 0 until tabCount) {
            val tab: TabLayout.Tab? = tlTabPersonal?.getTabAt(i)
            tab?.customView = null
            if (i == vpPersonal.currentItem) {
                tab?.customView = getTabView(activity, titleList[i], true)
            } else {
                tab?.customView = getTabView(activity, titleList[i], false)
            }
        }
    }

    private fun getTabView(context: Context?, title: CharSequence?, isSelected: Boolean): TextView? {
        if (context != null) {
            val tv: TextView = if (isSelected) {
                TextViewBold(context)
            } else {
                TextViewLight(context)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextColor(context.getColor(R.color.colorTitlePage))
            } else {
                tv.setTextColor(context.resources.getColor(R.color.colorTitlePage))
            }
            tv.text = title
            tv.gravity = Gravity.CENTER
            tv.maxLines = 1
            val textSize = resources.getDimension(R.dimen.title_tab_size) / resources.displayMetrics.density
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            return tv
        }
        return null
    }

    override fun onResume() {
        super.onResume()
        if (GlobalParams.isReloadDataPager && isPaused) {
            for(fragment in fragmentList){
                fragment.reloadData()
            }
            GlobalParams.isReloadDataPager = false
            isPaused = false
        }
    }

    override fun onPause() {
        isPaused = true
        super.onPause()
    }
}