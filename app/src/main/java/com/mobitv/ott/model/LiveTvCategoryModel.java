package com.mobitv.ott.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.mobitv.ott.model.LiveTvChannelModel;

public class LiveTvCategoryModel {

	@SerializedName("name")
	private String name;

	@SerializedName("icon")
	private String icon;

	@SerializedName("id")
	private int id;

	@SerializedName("channel_list")
	private List<LiveTvChannelModel> channelList;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setIcon(String icon){
		this.icon = icon;
	}

	public String getIcon(){
		return icon;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setChannelList(List<LiveTvChannelModel> channelList){
		this.channelList = channelList;
	}

	public List<LiveTvChannelModel> getChannelList(){
		return channelList;
	}
}