package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class ResendOtpNewPassResponseObject {

	@SerializedName("timeOut")
	private String timeout;

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
}