package com.mobitv.ott.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mobitv.ott.R
import com.mobitv.ott.activity.MainActivity
import com.mobitv.ott.adapter.ListLiveTvChannelRVAdapter
import com.mobitv.ott.adapter.OnItemClickListener
import com.mobitv.ott.dialog.MyAlertDialog
import com.mobitv.ott.model.LiveTvChannelModel
import com.mobitv.ott.other.Const
import com.mobitv.ott.other.GridItemChannelInsetDecoration
import kotlinx.android.synthetic.main.fragment_page_live_tv_category.*

class LiveTvCategoryPageFragment : Fragment(), OnItemClickListener {

    companion object {
        const val TAG = "live_tv_category"
        fun createInstance(
            _id: Int,
            name: String,
            icon: String,
            channelList: ArrayList<LiveTvChannelModel>
        ): LiveTvCategoryPageFragment {
            val bundle = Bundle()
            bundle.putInt(Const.ID, _id)
            bundle.putString(Const.NAME, name)
            bundle.putString(Const.ICON, icon)
            bundle.putParcelableArrayList(Const.CHANNEL_LIST, channelList)
            val shopFragment = LiveTvCategoryPageFragment()
            shopFragment.arguments = bundle
            return shopFragment
        }
    }

    private var listChannelAdapter: ListLiveTvChannelRVAdapter? = null
    private var channelList = ArrayList<LiveTvChannelModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_page_live_tv_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channelList = arguments?.getParcelableArrayList(Const.CHANNEL_LIST)!!
        listChannelAdapter = ListLiveTvChannelRVAdapter(activity!!, channelList)
        listChannelAdapter?.setItemClickListener(this)
        rvLiveTvChannel.layoutManager = GridLayoutManager(activity!!, 3)
        rvLiveTvChannel.addItemDecoration(GridItemChannelInsetDecoration(activity!!, 3))
        rvLiveTvChannel.adapter = listChannelAdapter
    }

    override fun onItemClick(tag: String, position: Int) {
        if (Const.DEBUG) {
            Log.d(TAG, position.toString())
        }
        if (activity is MainActivity) {
//            Toast.makeText(
//                context,
//                channelList[position].isCanWatched.toString(),
//                Toast.LENGTH_SHORT
//            ).show()
            var liveTvChannelModel = channelList[position];
//            MyAlertDialog.showDialogCanWatch(context)
            if (!liveTvChannelModel.isCanWatched) {
                MyAlertDialog.showDialogCanWatch(context)
            } else {
                (activity as MainActivity).startStreamingLiveTv(channelList[position])
            }
        }
    }

    override fun onItemLongClick(tag: String, position: Int) {
        //do nothing
    }

    var myAlertDialog: MyAlertDialog? = null
    private fun showDialog() {
        if (myAlertDialog == null) {
            myAlertDialog = MyAlertDialog(context,
                "Thông báo",
                "Vui lòng Đăng nhập để xem nội dung này.",
                "Đăng nhập",
                "",
                false,
                DialogInterface.OnClickListener { dialog, which ->
                    {

                    }
                },
                DialogInterface.OnClickListener { dialog, which ->
                    {

                    }
                })
        }
    }
}