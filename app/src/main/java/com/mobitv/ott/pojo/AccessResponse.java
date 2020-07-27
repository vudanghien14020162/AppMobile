package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class AccessResponse {

	@SerializedName("Active")
	private int activeCode;

	public int getActiveCode() {
		return activeCode;
	}
}