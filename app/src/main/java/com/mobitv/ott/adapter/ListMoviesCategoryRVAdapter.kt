package com.mobitv.ott.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobitv.ott.R
import com.mobitv.ott.model.VodCategoryModel
import com.mobitv.ott.adapter.ListMoviesRVAdapter

class ListMoviesCategoryRVAdapter : RecyclerView.Adapter<ListMoviesCategoryRVAdapter.MyViewHolder> {
    val tag = javaClass.name
    private var mContext: Context
    private var list: List<VodCategoryModel>
    private var itemClickListener: OnItemNestedListClickListener? = null
    private var viewPool: RecyclerView.RecycledViewPool
    private var widthContainer: Int

    constructor(context: Context, list: List<VodCategoryModel>, widthContainer: Int) {
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

        viewHolder.tvItemName.text = item.name
        viewHolder.rvItemVod.setRecycledViewPool(viewPool)
        viewHolder.rvItemVod.layoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.HORIZONTAL,
            false
        )
        viewHolder.rvItemVod.setHasFixedSize(true)
        val itemAdapter: ListMoviesRVAdapter
        if (pos == 0) {
            itemAdapter = ListMoviesRVAdapter(
                mContext,
                item.listFilm,
                widthContainer,
                ListMoviesRVAdapter.TYPE_FEATURE,
                item.baseURL
            )
            viewHolder.layoutItemTitle.visibility = View.GONE
        } else {
            itemAdapter = ListMoviesRVAdapter(
                mContext,
                item.listFilm,
                widthContainer,
                ListMoviesRVAdapter.TYPE_NORMAL_ITEM,
                item.baseURL
            )
            viewHolder.layoutItemTitle.visibility = View.VISIBLE
        }
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

    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvItemName: TextView
        val tvItemMore: TextView
        val rvItemVod: RecyclerView
        val layoutItemTitle : View

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