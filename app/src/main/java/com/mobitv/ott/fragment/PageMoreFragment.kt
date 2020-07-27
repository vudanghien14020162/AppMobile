package com.mobitv.ott.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobitv.ott.R
import com.mobitv.ott.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_tab_more.*

class PageMoreFragment : androidx.fragment.app.Fragment(), View.OnClickListener {

    companion object {
        const val TAG = "page_more"
        fun createInstance(): PageMoreFragment {
            return PageMoreFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab_more, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnPersonalPage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnPersonalPage ->{
                btnPersonalPage.isSelected = true
                (activity as MainActivity).goPersonal()
            }
        }
    }
}