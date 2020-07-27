package com.mobitv.ott.adapter

interface OnItemClickListener {
    fun onItemClick(tag: String, position: Int)
    fun onItemLongClick(tag: String, position: Int)
}