package com.mobitv.ott.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mobitv.ott.R
import com.mobitv.ott.model.SearchSuggestionModel

class ListStaticSearchRVAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<ListStaticSearchRVAdapter.MediaViewHolder> {
    private var tag: String
    private var mContext: Context
    private var list: List<SearchSuggestionModel>
    private var itemClickListener: OnItemClickListener? = null

    constructor(tag: String, context: Context, list: List<SearchSuggestionModel>) {
        this.tag = tag
        this.list = list
        this.mContext = context
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: MediaViewHolder, pos: Int) {
        val item = list[pos]
        viewHolder.tvItemSearch.text = item.title
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MediaViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_search, viewGroup, false)
        return MediaViewHolder(itemView)
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class MediaViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val tvItemSearch: TextView

        init {
            tvItemSearch = v.findViewById(R.id.tvItemSearch)
            tvItemSearch.setOnClickListener {
                if (itemClickListener != null) {
                    itemClickListener!!.onItemClick(tag, adapterPosition)
                }
            }
            tvItemSearch.setOnLongClickListener {
                if (itemClickListener != null) {
                    itemClickListener?.onItemLongClick(tag, adapterPosition)
                }
                false
            }
        }
    }
}