package com.mobitv.ott.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.mobitv.ott.R
import com.mobitv.ott.customview.ExpandableTextView
import com.mobitv.ott.model.CommentModel
import com.mobitv.ott.other.GlobalParams

class ListCommentRVAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<ListCommentRVAdapter.MyViewHolder> {
    val tag = javaClass.name
    private var mContext: Context
    private var list: ArrayList<CommentModel>
    private var itemClickListener: OnItemClickListener? = null
    private var margin = 0
    private var sparseBooleanArray : SparseBooleanArray

    constructor(context: Context, list: ArrayList<CommentModel>, sparseBooleanArray: SparseBooleanArray) {
        this.list = list
        this.mContext = context
        this.sparseBooleanArray = sparseBooleanArray
        margin = context.resources.getDimensionPixelSize(R.dimen.side_margin_common)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, pos: Int) {
        val item = list[pos]
        Glide.with(mContext)
            .load(item.loginDatum.customerDatum.avataUrl)
            .apply(RequestOptions().signature(ObjectKey(GlobalParams.VERSION_CODE)))
            .into(viewHolder.imvItemAvatar)
        val name : String
        if(item.loginDatum.customerDatum.firstname.isNullOrEmpty()){
            viewHolder.tvItemUserName.text = item.loginDatum.tel
        }else {
            name = item.loginDatum.customerDatum.firstname  + " "+ item.loginDatum.customerDatum.lastname
            viewHolder.tvItemUserName.text = name
        }
        viewHolder.tvItemUserComment.setText(item.content, sparseBooleanArray, pos)

        val params = viewHolder.layoutContentComment.layoutParams as LinearLayout.LayoutParams
        params.bottomMargin = margin
        params.marginEnd = margin
        if(item.level == 0){
            viewHolder.vItemSeparate.visibility = View.VISIBLE
            params.marginStart = margin
        }else{
            viewHolder.vItemSeparate.visibility = View.INVISIBLE
            params.marginStart = item.level * viewHolder.imvItemAvatar.width + margin
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.item_comment,
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
        val imvItemAvatar: ImageView
        val tvItemUserName: TextView
        val tvItemUserComment: ExpandableTextView
        val vItemSeparate: View
        val layoutContentComment: View

        init {
            layoutItemRoot = v.findViewById(R.id.layoutItemRoot)
            imvItemAvatar = v.findViewById(R.id.imvItemAvatar)
            tvItemUserName = v.findViewById(R.id.tvItemUserName)
            tvItemUserComment = v.findViewById(R.id.tvItemUserComment)
            layoutContentComment = v.findViewById(R.id.layoutContentComment)
            vItemSeparate = v.findViewById(R.id.vItemSeparate)
            v.setOnClickListener {
                itemClickListener?.onItemClick(tag, adapterPosition)
            }
        }
    }
}