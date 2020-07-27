package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListVodIdParams{

	public ListVodIdParams(List<Integer> listId) {
		this.listId = listId;
	}

	@SerializedName("list_id")
	private List<Integer> listId;

	public void setListId(List<Integer> listId){
		this.listId = listId;
	}

	public List<Integer> getListId(){
		return listId;
	}
}