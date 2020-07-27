package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class ConfirmOtpNewPassParams {

	@SerializedName("code")
	private String code;

	@SerializedName("tel")
	private String tel;

	public ConfirmOtpNewPassParams(String code, String tel) {
		this.code = code;
		this.tel = tel;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
}