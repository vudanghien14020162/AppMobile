package com.mobitv.ott.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.mobitv.ott.R
import com.mobitv.ott.activity.StartActivity
import com.mobitv.ott.other.GlobalParams
import kotlinx.android.synthetic.main.fragment_page_slide.*

class SlidePageFragment : Fragment() {

    companion object {
        const val TAG = "slide"
        fun createInstance(image: String): SlidePageFragment {
            val bundle = Bundle()
            bundle.putString("image", image)
            val shopFragment = SlidePageFragment()
            shopFragment.arguments = bundle
            return shopFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_slide, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val image = arguments?.getString("image")
        Glide.with(activity!!)
            .load(image)
            .apply(RequestOptions().signature(ObjectKey(GlobalParams.VERSION_CODE)))
            .into(imgSlide)
    }
}