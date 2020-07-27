package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class VodFavouriteParams{

	public VodFavouriteParams(int vodID) {
		this.vodID = vodID;
	}

	@SerializedName("vodID")
	private int vodID;

	public void setVodID(int vodID){
		this.vodID = vodID;
	}

	public int getVodID(){
		return vodID;
	}
}