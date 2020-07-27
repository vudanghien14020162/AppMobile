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

class ListTvShowsRVAdapter :
    androidx.recyclerview.widget.RecyclerView.Adapter<ListTvShowsRVAdapter.MyViewHolder> {
    companion object {
        const val TYPE_FEATURE = 0
        const val TYPE_NORMAL_ITEM = 1
        const val TYPE_NORMAL_MORE = 2
    }

    val tag = javaClass.name
    private var mContext: Context
    private var list: ArrayList<VodModel>
    private var itemClickListener: OnItemNestedListClickListener? = null
    private var widthItem: Int
    private var type: Int = -1
    private var baseURL: String

    constructor(
        context: Context,
        list: ArrayList<VodModel>,
        widthContainer: Int,
        type: Int,
        baseURL: String
    ) {
        this.list = list
        this.mContext = context
        this.type = type
        if (type == TYPE_FEATURE) {
            widthItem =
                ((widthContainer - 3 * mContext.resources.getDimension(R.dimen.side_margin_common)) / 2.2f).toInt()
        } else {
            widthItem =
                ((widthContainer - 2 * mContext.resources.getDimension(R.dimen.side_margin_common)) / 1.2f).toInt()
        }
//        vtvcab
//        widthItem =
//            ((widthContainer - 2 * mContext.resources.getDimension(R.dimen.side_margin_common)) / 1.2f).toInt()
//        if (type == TYPE_FEATURE) {
//            widthItem = widthItem
//        } else {
//            widthItem =
//                ((widthItem - mContext.resources.getDimension(R.dimen.side_margin_common)) / 2).toInt()
//        }
        //
        this.baseURL = baseURL
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, pos: Int) {
        val item = list[pos]
        val params =
            viewHolder.itemView.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams
        params.width = widthItem
        params.marginEnd = mContext.resources.getDimension(R.dimen.side_margin_common).toInt()
        if (pos != 0) {
            params.marginStart = 0
        } else {
            params.marginStart = mContext.resources.getDimension(R.dimen.side_margin_common).toInt()
        }
        viewHolder.itemView.layoutParams = params
        //old
//        val urlDisplay = if (this.type == TYPE_FEATURE) {
//            baseURL + item.iconUrl
//        } else {
//            baseURL + item.imageUrl
//        }
        //vtvcab
        val urlDisplay = if (this.type == TYPE_FEATURE) {
            baseURL + item.imageUrl
        } else {
            baseURL + item.iconUrl
        }
        if (type == TYPE_FEATURE) {
            Glide.with(mContext)
                .load(urlDisplay)
                .apply(
                    RequestOptions().placeholder(R.drawable.img_placeholder_portrait).error(R.drawable.img_placeholder_portrait).signature(
                        ObjectKey(GlobalParams.VERSION_CODE)
                    )
                )
                .into(viewHolder.imgItemPoster)
        } else {
            Glide.with(mContext)
                .load(urlDisplay)
                .apply(
                    RequestOptions().placeholder(R.drawable.img_placeholder_landscape).error(R.drawable.img_placeholder_landscape).signature(
                        ObjectKey(GlobalParams.VERSION_CODE)
                    )
                )
                .into(viewHolder.imgItemPoster)
        }
        viewHolder.tvItemName.text = item.title

        viewHolder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(
                tag,
                viewHolder.adapterPosition,
                list[viewHolder.adapterPosition].id
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
//        if (type != TYPE_FEATURE && position == list.size - 1) {
//            return TYPE_NORMAL_MORE
//        }
        return this.type
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView: View = if (i == TYPE_FEATURE) {
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_vod_vertical_with_text, viewGroup, false)
        } else {
            if (i == TYPE_NORMAL_ITEM) {
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_vod_horizontal_with_text, viewGroup, false)
            } else {
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_more_horizontal, viewGroup, false)
            }
        }
        return MyViewHolder(itemView)
    }

    fun setItemClickListener(itemClickListener: OnItemNestedListClickListener?) {
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
        }
    }
}