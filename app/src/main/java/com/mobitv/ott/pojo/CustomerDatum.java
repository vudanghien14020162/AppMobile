package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class CustomerDatum{

	@SerializedName("firstname")
	private String firstname;

	@SerializedName("avata_url")
	private String avataUrl;

	@SerializedName("lastname")
	private String lastname;

	public void setFirstname(String firstname){
		this.firstname = firstname;
	}

	public String getFirstname(){
		return firstname;
	}

	public void setAvataUrl(String avataUrl){
		this.avataUrl = avataUrl;
	}

	public String getAvataUrl(){
		return avataUrl;
	}

	public void setLastname(String lastname){
		this.lastname = lastname;
	}

	public String getLastname(){
		return lastname;
	}
}