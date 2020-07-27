package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class ConfirmOtpSignUpParams {

	@SerializedName("code")
	private String code;

	@SerializedName("os")
	private String os;

	@SerializedName("googleappid")
	private String googleappid;

	@SerializedName("hdmi")
	private int hdmi;

	@SerializedName("tel")
	private String tel;

	@SerializedName("appversion")
	private String appversion;

	@SerializedName("ntype")
	private int ntype;

	@SerializedName("deviceId")
	private String deviceId;

	public ConfirmOtpSignUpParams(String code, String os, String googleappid, int hdmi, String tel, String appversion, int ntype, String deviceId) {
		this.code = code;
		this.os = os;
		this.googleappid = googleappid;
		this.hdmi = hdmi;
		this.tel = tel;
		this.appversion = appversion;
		this.ntype = ntype;
		this.deviceId = deviceId;
	}

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public void setOs(String os){
		this.os = os;
	}

	public String getOs(){
		return os;
	}

	public void setGoogleappid(String googleappid){
		this.googleappid = googleappid;
	}

	public String getGoogleappid(){
		return googleappid;
	}

	public void setHdmi(int hdmi){
		this.hdmi = hdmi;
	}

	public int getHdmi(){
		return hdmi;
	}

	public void setTel(String tel){
		this.tel = tel;
	}

	public String getTel(){
		return tel;
	}

	public void setAppversion(String appversion){
		this.appversion = appversion;
	}

	public String getAppversion(){
		return appversion;
	}

	public void setNtype(int ntype){
		this.ntype = ntype;
	}

	public int getNtype(){
		return ntype;
	}

	public void setDeviceId(String deviceId){
		this.deviceId = deviceId;
	}

	public String getDeviceId(){
		return deviceId;
	}
}