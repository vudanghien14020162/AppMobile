package com.mobitv.ott.model;

import com.google.gson.annotations.SerializedName;

public class SearchResultModel {

	@SerializedName("icon_url")
	private String iconUrl;

	@SerializedName("encryption")
	private int encryption;

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

	@SerializedName("stream_url")
	private String streamUrl;

	@SerializedName("channel_number")
	private int channelNumber;

	private String type;

	public void setIconUrl(String iconUrl){
		this.iconUrl = iconUrl;
	}

	public String getIconUrl(){
		return iconUrl;
	}

	public void setEncryption(int encryption){
		this.encryption = encryption;
	}

	public int getEncryption(){
		return encryption;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setStreamUrl(String streamUrl){
		this.streamUrl = streamUrl;
	}

	public String getStreamUrl(){
		return streamUrl;
	}

	public void setChannelNumber(int channelNumber){
		this.channelNumber = channelNumber;
	}

	public int getChannelNumber(){
		return channelNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}