package com.mobitv.ott.adapter

interface OnItemNestedListClickListener {
    fun onHeaderClick(tag: String, position: Int, id: String)
    fun onItemClick(tag: String, position: Int, id: Int)
    fun onItemLongClick(tag: String, position: Int, id: Int)
    fun onMoreClick(tag: String, catID: String, categoryName: String)
}