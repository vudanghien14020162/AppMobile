package com.mobitv.ott.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobitv.ott.R
import com.mobitv.ott.model.SearchResultCategoryModel

class ListSearchResultCategoryRVAdapter : RecyclerView.Adapter<ListSearchResultCategoryRVAdapter.MyViewHolder> {
    val tag = javaClass.name
    private var mContext: Context
    private var list: List<SearchResultCategoryModel>
    private var itemClickListener: ListSearchResultRVAdapter.OnItemClickListener? = null
    private var viewPool: androidx.recyclerview.widget.RecyclerView.RecycledViewPool
    private var widthContainer: Int

    constructor(context: Context, list: List<SearchResultCategoryModel>, widthContainer: Int) {
        this.list = list
        this.mContext = context
        this.widthContainer = widthContainer
        viewPool = RecyclerView.RecycledViewPool()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, pos: Int) {
        val item = list[pos]
        viewHolder.tvItemName.text = item.title
        viewHolder.rvItemVod.setRecycledViewPool(viewPool)
        viewHolder.rvItemVod.layoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.HORIZONTAL,
            false
        )
        viewHolder.rvItemVod.setHasFixedSize(true)
        val itemAdapter = ListSearchResultRVAdapter(
            mContext,
            item.list,
            widthContainer,
            pos,
            item.type
        )
        itemAdapter.setItemClickListener(itemClickListener)
        viewHolder.rvItemVod.adapter = itemAdapter
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_search_result_category, viewGroup, false)
        return MyViewHolder(itemView)
    }

    fun setItemClickListener(itemClickListener: ListSearchResultRVAdapter.OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    inner class MyViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val tvItemName: TextView
        val rvItemVod: androidx.recyclerview.widget.RecyclerView

        init {
            tvItemName = v.findViewById(R.id.tvItemName)
            rvItemVod = v.findViewById(R.id.rvItemVod)
        }
    }

    fun setContainerWidth(width: Int) {
        widthContainer = width
    }
}