package com.mobitv.ott.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.mobitv.ott.R
import com.mobitv.ott.model.Episode
import com.mobitv.ott.model.VodModel
import com.mobitv.ott.other.GlobalParams

class ListEpisodeRVAdapter : RecyclerView.Adapter<ListEpisodeRVAdapter.MyViewHolder> {
    val tag = javaClass.name
    private var mContext: Context
    private var list: ArrayList<VodModel>
    private var itemClickListener: OnItemClickListener? = null

    constructor(context: Context, list: ArrayList<VodModel>) {
        this.list = list
        this.mContext = context
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(viewHolder: MyViewHolder, pos: Int) {
        val item = list[pos]
        Glide.with(mContext)
            .load(item.iconUrl)
            .apply(RequestOptions().placeholder(R.drawable.img_placeholder_landscape).error(R.drawable.img_placeholder_landscape).signature(ObjectKey(GlobalParams.VERSION_CODE)))
            .into(viewHolder.imgItemPoster)
        viewHolder.tvItemName.text = item.title
        val duration =  item.duration.toString() + " " + mContext.getString(R.string.minute)
        viewHolder.tvItemDuration.text = duration
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.item_episode,
                viewGroup,
                false
            )
        )
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val layoutItemRoot: ViewGroup
        val imgItemPoster: ImageView
        val tvItemName: TextView
        val tvItemDuration: TextView

        init {
            layoutItemRoot = v.findViewById(R.id.layoutItemRoot)
            imgItemPoster = v.findViewById(R.id.imgItemPoster)
            tvItemName = v.findViewById(R.id.tvItemName)
            tvItemDuration = v.findViewById(R.id.tvItemDuration)
            v.setOnClickListener {
                itemClickListener?.onItemClick(tag, adapterPosition)
            }
        }
    }
}