package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class BasicSettings {

	@SerializedName("assets_url")
	private String assetsUrl;

	@SerializedName("logo_url")
	private String logoUrl;

	@SerializedName("background_url")
	private String backgroundUrl;

	@SerializedName("vod_background_url")
	private String vodBackgroundUrl;

	@SerializedName("company_url")
	private String companyUrl;

	@SerializedName("new_encryption_key")
	private String newEncryptionKey;

	@SerializedName("android_version_code")
	private int versionCode;

	public void setAssetsUrl(String assetsUrl){
		this.assetsUrl = assetsUrl;
	}

	public String getAssetsUrl(){
		return assetsUrl;
	}

	public void setLogoUrl(String logoUrl){
		this.logoUrl = logoUrl;
	}

	public String getLogoUrl(){
		return logoUrl;
	}

	public void setBackgroundUrl(String backgroundUrl){
		this.backgroundUrl = backgroundUrl;
	}

	public String getBackgroundUrl(){
		return backgroundUrl;
	}

	public void setVodBackgroundUrl(String vodBackgroundUrl){
		this.vodBackgroundUrl = vodBackgroundUrl;
	}

	public String getVodBackgroundUrl(){
		return vodBackgroundUrl;
	}

	public void setCompanyUrl(String companyUrl){
		this.companyUrl = companyUrl;
	}

	public String getCompanyUrl(){
		return companyUrl;
	}

	public void setNewEncryptionKey(String newEncryptionKey){
		this.newEncryptionKey = newEncryptionKey;
	}

	public String getNewEncryptionKey(){
		return newEncryptionKey;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
}