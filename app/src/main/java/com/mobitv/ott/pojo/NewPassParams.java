package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class NewPassParams{

	@SerializedName("code")
	private String code;

	@SerializedName("newPassword")
	private String newPassword;

	@SerializedName("tel")
	private String tel;

	public NewPassParams(String code, String newPassword, String tel) {
		this.code = code;
		this.newPassword = newPassword;
		this.tel = tel;
	}

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public void setNewPassword(String newPassword){
		this.newPassword = newPassword;
	}

	public String getNewPassword(){
		return newPassword;
	}

	public void setTel(String tel){
		this.tel = tel;
	}

	public String getTel(){
		return tel;
	}

	@Override
 	public String toString(){
		return 
			"NewPassParams{" + 
			"code = '" + code + '\'' + 
			",newPassword = '" + newPassword + '\'' + 
			",tel = '" + tel + '\'' + 
			"}";
		}
}