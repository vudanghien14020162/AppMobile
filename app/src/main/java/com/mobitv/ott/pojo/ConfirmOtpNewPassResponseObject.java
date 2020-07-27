package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class ConfirmOtpNewPassResponseObject {

	@SerializedName("encryption_key")
	private String encryptionKey;

	@SerializedName("auth_key")
	private String authKey;

	public void setEncryptionKey(String encryptionKey){
		this.encryptionKey = encryptionKey;
	}

	public String getEncryptionKey(){
		return encryptionKey;
	}

	public void setAuthKey(String authKey){
		this.authKey = authKey;
	}

	public String getAuthKey(){
		return authKey;
	}
}