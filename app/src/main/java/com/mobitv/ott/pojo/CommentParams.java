package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class CommentParams{

	@SerializedName("vodID")
	private String vodID;

	@SerializedName("rootID")
	private String rootID;

	@SerializedName("content")
	private String content;

	public CommentParams(String vodID, String rootID, String content) {
		this.vodID = vodID;
		this.rootID = rootID;
		this.content = content;
	}

	public void setVodID(String vodID){
		this.vodID = vodID;
	}

	public String getVodID(){
		return vodID;
	}

	public void setRootID(String rootID){
		this.rootID = rootID;
	}

	public String getRootID(){
		return rootID;
	}

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return content;
	}
}