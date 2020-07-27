package com.mobitv.ott.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mobitv.ott.R
import com.mobitv.ott.customview.CustomTypefaceSpan
import com.mobitv.ott.model.DailyEPGModel
import com.mobitv.ott.other.AppUtils

class DailyEPGRVAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<DailyEPGRVAdapter.MediaViewHolder> {
    val tag: String = javaClass.name
    private var mContext: Context
    private var list: List<DailyEPGModel>
    private var itemClickListener: OnItemClickListener? = null

    constructor(context: Context, list: List<DailyEPGModel>) {
        this.list = list
        this.mContext = context
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: MediaViewHolder, pos: Int) {
        val item = list[pos]
        viewHolder.tvItemTime.text = AppUtils.formatDateTimeToHour(item.programstart)
        if (item.shortname.isNullOrEmpty() || item.shortname.isBlank()) {
            viewHolder.tvItemName.text = item.title
        } else {
            val content = item.title + ": " + item.shortname
            val ss = SpannableString(content)
            ss.setSpan(
                CustomTypefaceSpan("", Typeface.createFromAsset(mContext.assets, "app/SF-Pro-Display-RegularItalic.otf")),
                item.title.length + 1, content.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            viewHolder.tvItemName.text = ss
        }
        if (pos % 2 == 0) {
            viewHolder.layoutItemRoot.setBackgroundColor(Color.parseColor("#d6d6d6"))
        } else {
            viewHolder.layoutItemRoot.setBackgroundColor(Color.TRANSPARENT)
        }
        if (item.isPlaying) {
            viewHolder.tvItemTime.typeface = Typeface.createFromAsset(mContext.assets, "app/SF-Pro-Display-Bold.otf")
            viewHolder.tvItemTime.setTextColor(Color.parseColor("#e34051"))
            viewHolder.tvItemName.setTextColor(Color.parseColor("#e34051"))
            viewHolder.vItemPlaying.visibility = View.VISIBLE
        } else {
            viewHolder.tvItemTime.typeface = Typeface.createFromAsset(mContext.assets, "app/SF-Pro-Display-Light.otf")
            viewHolder.tvItemTime.setTextColor(Color.parseColor("#666666"))
            viewHolder.vItemPlaying.visibility = View.GONE
            if (item.isDisable) {
                viewHolder.tvItemName.setTextColor(Color.parseColor("#c8c8c8"))
            } else {
                viewHolder.tvItemName.setTextColor(Color.parseColor("#666666"))
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MediaViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_daily_epg, viewGroup, false)
        return MediaViewHolder(itemView)
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class MediaViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val layoutItemRoot: ViewGroup
        val tvItemName: TextView
        val tvItemTime: TextView
        val vItemPlaying: View

        init {
            layoutItemRoot = v.findViewById(R.id.layoutItemRoot)
            tvItemName = v.findViewById(R.id.tvItemName)
            tvItemTime = v.findViewById(R.id.tvItemTime)
            vItemPlaying = v.findViewById(R.id.vItemPlaying)
            layoutItemRoot.setOnClickListener {
                if (itemClickListener != null) {
                    itemClickListener!!.onItemClick(tag, adapterPosition)
                }
            }
            layoutItemRoot.setOnLongClickListener {
                if (itemClickListener != null) {
                    itemClickListener?.onItemLongClick(tag, adapterPosition)
                }
                false
            }
        }
    }
}