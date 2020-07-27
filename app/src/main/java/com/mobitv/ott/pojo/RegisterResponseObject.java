package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class RegisterResponseObject {

	@SerializedName("customer_id")
	private int customerId;

	@SerializedName("username")
	private String username;

	@SerializedName("timeOut")
	private String timeout;

	public void setCustomerId(int customerId){
		this.customerId = customerId;
	}

	public int getCustomerId(){
		return customerId;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
}