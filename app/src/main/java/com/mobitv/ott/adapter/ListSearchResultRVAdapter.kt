package com.mobitv.ott.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.mobitv.ott.R
import com.mobitv.ott.model.SearchResultModel
import com.mobitv.ott.other.GlobalParams

class ListSearchResultRVAdapter : RecyclerView.Adapter<ListSearchResultRVAdapter.MyViewHolder> {
    interface OnItemClickListener {
        fun onItemClick(tag: String, parentPos: Int, position: Int)
        fun onItemLongClick(tag: String, parentPos: Int, position: Int)
    }
    val tag = javaClass.name
    private var mContext: Context
    private var list: List<SearchResultModel>
    private var itemClickListener: OnItemClickListener? = null
    private var widthItem: Int
    private var parentPos: Int
    private var parentType: String

    constructor(context: Context, list: List<SearchResultModel>, widthContainer: Int, parentPos: Int, parentType: String) {
        this.list = list
        this.mContext = context
        widthItem =
            ((widthContainer) / 2.2f).toInt()
        this.parentPos = parentPos
        this.parentType = parentType
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, pos: Int) {
        val item = list[pos]
        item.type = parentType
        val params = viewHolder.itemView.layoutParams as RecyclerView.LayoutParams
        params.width = widthItem
        if (pos == 0) {
            params.marginStart = 0
        } else {
            params.marginStart = mContext.resources.getDimension(R.dimen.side_margin_common).toInt()
        }
        Glide.with(mContext)
            .load(item.iconUrl)
            .apply(RequestOptions().signature(ObjectKey(GlobalParams.VERSION_CODE)))
            .into(viewHolder.imgItemPoster)
        viewHolder.tvItemName.text = item.title
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.item_search_result,
                viewGroup,
                false
            )
        )
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
                itemClickListener?.onItemClick(tag, parentPos, adapterPosition)
            }
        }
    }
}