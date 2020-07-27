package com.mobitv.ott.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.mobitv.ott.R
import com.mobitv.ott.activity.StartActivity
import com.mobitv.ott.model.LiveTvChannelModel
import com.mobitv.ott.other.GlobalParams

class ListLiveTvChannelRVAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<ListLiveTvChannelRVAdapter.MediaViewHolder> {
    val tag: String = javaClass.name
    private var mContext: Context
    private var list: List<LiveTvChannelModel>
    private var itemClickListener: OnItemClickListener? = null

    constructor(context: Context, list: List<LiveTvChannelModel>) {
        this.list = list
        this.mContext = context
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: MediaViewHolder, pos: Int) {
        val item = list[pos]
        Glide.with(mContext)
                .load(item.iconUrl)
                .apply(RequestOptions().signature(ObjectKey(GlobalParams.VERSION_CODE)))
                .into(viewHolder.imgItem)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MediaViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_live_tv_channel, viewGroup, false)
        return MediaViewHolder(itemView)
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class MediaViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val itemLayoutRoot: ViewGroup
        val imgItem: ImageView

        init {
            itemLayoutRoot = v.findViewById(R.id.itemLayoutRoot)
            imgItem = v.findViewById(R.id.imgItem)
            v.setOnClickListener {
                if (itemClickListener != null) {
                    itemClickListener!!.onItemClick(tag, adapterPosition)
                }
            }
            v.setOnLongClickListener {
                if (itemClickListener != null) {
                    itemClickListener?.onItemLongClick(tag, adapterPosition)
                }
                false
            }
        }
    }
}