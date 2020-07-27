package com.mobitv.ott.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mobitv.ott.R
import com.mobitv.ott.adapter.ListVodRVAdapter
import com.mobitv.ott.adapter.OnItemClickListener
import com.mobitv.ott.model.VodModel
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.GridItemPersonalInsetDecoration
import kotlinx.android.synthetic.main.fragment_page_personal.*

class PersonalDownloadedFragment : CommonPageFragment(), OnItemClickListener {

    companion object {
        const val TAG = "personal_downloaded"
        fun createInstance(): PersonalDownloadedFragment {
            return PersonalDownloadedFragment()
        }
    }

    private var listVodRVAdapter: ListVodRVAdapter? = null
    private var vodList = ArrayList<VodModel>()

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
    }

    override fun reloadData() {
    }

    override fun onItemClick(tag: String, position: Int) {
        if (Const.DEBUG) {
            Log.d(TAG, position.toString())
        }
//        val intent = Intent(activity!!, VodDetailsActivity::class.java)
//        intent.putExtra(Const.ID, vodList[position].id)
//        startActivity(intent)
//        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onItemLongClick(tag: String, position: Int) {
        //do nothing
    }

}