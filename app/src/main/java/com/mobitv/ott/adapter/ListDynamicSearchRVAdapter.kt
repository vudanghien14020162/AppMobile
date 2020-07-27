package com.mobitv.ott.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mobitv.ott.R
import com.mobitv.ott.model.SearchSuggestionModel
import com.mobitv.ott.other.IndexWrapper
import java.util.regex.Pattern
import android.R.attr.start


class ListDynamicSearchRVAdapter :
    androidx.recyclerview.widget.RecyclerView.Adapter<ListDynamicSearchRVAdapter.MediaViewHolder> {
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
        val index = findIndexes(item.highlight, item.titleAlias)
        if(index != null) {
            val spannable = SpannableString(item.title)
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#d82731")),
                index.start,
                index.end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            viewHolder.tvItemSearch.text = spannable
        }else{
            viewHolder.tvItemSearch.text = item.title
        }
    }

    private fun findIndexes(highlight: String, searchString: String): IndexWrapper? {
        val wrapper = IndexWrapper()
        val start = searchString.indexOf(highlight)
        var end = start
        if (start >= 0) {
            for (i in 0 until highlight.length) {
                val a = highlight[i]
                val b = searchString[start + i]
                if (a == b) {
                    end++
                }
            }
            wrapper.start = start
            wrapper.end = end
            return wrapper
        }
        return null
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