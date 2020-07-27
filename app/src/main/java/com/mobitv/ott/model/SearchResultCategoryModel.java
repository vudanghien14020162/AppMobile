package com.mobitv.ott.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResultCategoryModel {

	@SerializedName("type")
	private String type;

	@SerializedName("title")
	private String title;

	@SerializedName("list")
	private List<SearchResultModel> listModel;

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setList(List<SearchResultModel> listModel){
		this.listModel = listModel;
	}

	public List<SearchResultModel> getList(){
		return listModel;
	}
}