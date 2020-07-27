package com.mobitv.ott.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mobitv.ott.R
import com.mobitv.ott.model.VodCategoryModel
import com.mobitv.ott.adapter.ListTvShowsRVAdapter

class ListTvShowsCategoryRVAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<ListTvShowsCategoryRVAdapter.MyViewHolder> {
    val tag = javaClass.name
    private var mContext: Context
    private var list: List<VodCategoryModel>
    private var itemClickListener: OnItemNestedListClickListener? = null
    private var viewPool: androidx.recyclerview.widget.RecyclerView.RecycledViewPool
    private var widthContainer: Int

    constructor(context: Context, list: List<VodCategoryModel>, widthContainer: Int) {
        this.list = list
        this.mContext = context
        this.widthContainer = widthContainer
        viewPool = androidx.recyclerview.widget.RecyclerView.RecycledViewPool()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, pos: Int) {
        val item = list[pos]

        viewHolder.tvItemName.text = item.name
        viewHolder.rvItemVod.setRecycledViewPool(viewPool)
        viewHolder.rvItemVod.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            mContext,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        viewHolder.rvItemVod.setHasFixedSize(true)
        val itemAdapter: ListTvShowsRVAdapter
        if (pos == 0) {
            itemAdapter = ListTvShowsRVAdapter(
                mContext,
                item.listFilm,
                widthContainer,
                ListMoviesRVAdapter.TYPE_FEATURE,
                item.baseURL
            )
            viewHolder.layoutItemTitle.visibility = View.GONE
        } else {
            itemAdapter = ListTvShowsRVAdapter(
                mContext,
                item.listFilm,
                widthContainer,
                ListMoviesRVAdapter.TYPE_NORMAL_ITEM,
                item.baseURL
            )
            viewHolder.layoutItemTitle.visibility = View.VISIBLE
        }
//        if (pos == 0) {
//            itemAdapter = ListTvShowsRVAdapter(
//                mContext,
//                item.listFilm,
//                widthContainer,
//                ListMoviesRVAdapter.TYPE_FEATURE,
//                item.baseURL
//            )
//            viewHolder.layoutItemTitle.visibility = View.GONE
//        } else {
//            itemAdapter = ListTvShowsRVAdapter(
//                mContext,
//                item.listFilm,
//                widthContainer,
//                ListMoviesRVAdapter.TYPE_NORMAL_ITEM,
//                item.baseURL
//            )
//            viewHolder.layoutItemTitle.visibility = View.VISIBLE
//        }
        itemAdapter.setItemClickListener(itemClickListener)
        viewHolder.rvItemVod.adapter = itemAdapter
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_vod_category, viewGroup, false)
        return MyViewHolder(itemView)
    }

    fun setItemClickListener(itemClickListener: OnItemNestedListClickListener?) {
        this.itemClickListener = itemClickListener
    }

    inner class MyViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val tvItemName: TextView
        val tvItemMore: TextView
        val rvItemVod: androidx.recyclerview.widget.RecyclerView
        val layoutItemTitle: View

        init {
            layoutItemTitle = v.findViewById(R.id.layoutItemTitle)
            tvItemName = v.findViewById(R.id.tvItemName)
            tvItemMore = v.findViewById(R.id.tvItemMore)
            rvItemVod = v.findViewById(R.id.rvItemVod)
            tvItemName.setOnClickListener {
                itemClickListener?.onHeaderClick(tag, adapterPosition, list[adapterPosition].id)
            }
            tvItemMore.setOnClickListener {
                itemClickListener?.onMoreClick(tag, list[adapterPosition].id, list[adapterPosition].name)
            }
        }
    }
}