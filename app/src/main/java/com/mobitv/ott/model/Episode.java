package com.mobitv.ott.model;

import com.google.gson.annotations.SerializedName;

public class Episode{

	@SerializedName("duration")
	private int duration;

	@SerializedName("isavailable")
	private boolean isavailable;

	@SerializedName("encryption")
	private int encryption;

	@SerializedName("description")
	private String description;

	@SerializedName("encryption_url")
	private String encryptionUrl;

	@SerializedName("vod_type")
	private String vodType;

	@SerializedName("id")
	private int id;

	@SerializedName("number_in_parent")
	private int numberInParent;

	@SerializedName("title")
	private String title;

	@SerializedName("token_url")
	private String tokenUrl;

	@SerializedName("url")
	private String url;

	@SerializedName("token")
	private boolean token;

	@SerializedName("clicks")
	private int clicks;

	public void setDuration(int duration){
		this.duration = duration;
	}

	public int getDuration(){
		return duration;
	}

	public void setIsavailable(boolean isavailable){
		this.isavailable = isavailable;
	}

	public boolean isIsavailable(){
		return isavailable;
	}

	public void setEncryption(int encryption){
		this.encryption = encryption;
	}

	public int getEncryption(){
		return encryption;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setEncryptionUrl(String encryptionUrl){
		this.encryptionUrl = encryptionUrl;
	}

	public String getEncryptionUrl(){
		return encryptionUrl;
	}

	public void setVodType(String vodType){
		this.vodType = vodType;
	}

	public String getVodType(){
		return vodType;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setTokenUrl(String tokenUrl){
		this.tokenUrl = tokenUrl;
	}

	public String getTokenUrl(){
		return tokenUrl;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}

	public void setToken(boolean token){
		this.token = token;
	}

	public boolean isToken(){
		return token;
	}

	public int getNumberInParent() {
		return numberInParent;
	}

	public void setNumberInParent(int numberInParent) {
		this.numberInParent = numberInParent;
	}

	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}

	@Override
	public String toString() {
		return "Episode{" +
				"duration=" + duration +
				", isavailable=" + isavailable +
				", encryption=" + encryption +
				", description='" + description + '\'' +
				", encryptionUrl='" + encryptionUrl + '\'' +
				", vodType='" + vodType + '\'' +
				", id=" + id +
				", numberInParent=" + numberInParent +
				", title='" + title + '\'' +
				", tokenUrl='" + tokenUrl + '\'' +
				", url='" + url + '\'' +
				", token=" + token +
				", clicks=" + clicks +
				'}';
	}
}