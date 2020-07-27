package com.mobitv.ott.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.mobitv.ott.R
import com.mobitv.ott.model.VodModel
import com.mobitv.ott.other.GlobalParams

class ListVodRVAdapter :
    androidx.recyclerview.widget.RecyclerView.Adapter<ListVodRVAdapter.MyViewHolder> {
    companion object {
        const val TYPE_WITH_TEXT = 0
        const val TYPE_NO_TEXT = 1
    }

    val tag = javaClass.name
    private var mContext: Context
    private var list: ArrayList<VodModel>
    private var itemClickListener: OnItemClickListener? = null
    private var showText = true

    constructor(context: Context, list: ArrayList<VodModel>, showText: Boolean) {
        this.list = list
        this.mContext = context
        this.showText = showText
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (showText) {
            TYPE_WITH_TEXT
        } else {
            TYPE_NO_TEXT
        }
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, pos: Int) {
        val item = list[pos]
        Glide.with(mContext)
            .load(item.iconUrl)
            .apply(RequestOptions().placeholder(R.drawable.img_placeholder_portrait).error(R.drawable.img_placeholder_portrait).signature(ObjectKey(GlobalParams.VERSION_CODE)))
            .into(viewHolder.imgItemPoster)
        viewHolder.tvItemName.text = item.title
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        if (i == TYPE_WITH_TEXT) {
            return MyViewHolder(
                LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.item_vod_vertical_with_text,
                    viewGroup,
                    false
                )
            )
        } else {
            return MyViewHolder(
                LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.item_vod_vertical_no_text,
                    viewGroup,
                    false
                )
            )
        }
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    inner class MyViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val layoutItemRoot: ViewGroup
        val imgItemPoster: ImageView
        val tvItemName: TextView

        init {
            layoutItemRoot = v.findViewById(R.id.layoutItemRoot)
            imgItemPoster = v.findViewById(R.id.imgItemPoster)
            tvItemName = v.findViewById(R.id.tvItemName)
            v.setOnClickListener {
                itemClickListener?.onItemClick(tag, adapterPosition)
            }
        }
    }
}