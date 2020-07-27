package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class UpdateClickResponseObject {

	@SerializedName("clicks")
	private int clicks;

	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}
}