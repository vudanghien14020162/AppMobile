package com.mobitv.ott.model;

import com.google.gson.annotations.SerializedName;

public class SearchSuggestionModel {

	@SerializedName("title_alias")
	private String titleAlias;

	@SerializedName("counts")
	private int counts;

	@SerializedName("type")
	private String type;

	@SerializedName("title")
	private String title;

	private String highlight;

	public void setTitleAlias(String titleAlias){
		this.titleAlias = titleAlias;
	}

	public String getTitleAlias(){
		return titleAlias;
	}

	public void setCounts(int counts){
		this.counts = counts;
	}

	public int getCounts(){
		return counts;
	}

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

	public String getHighlight() {
		return highlight;
	}

	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}
}