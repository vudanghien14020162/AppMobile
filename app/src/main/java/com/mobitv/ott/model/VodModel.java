package com.mobitv.ott.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class VodModel implements Parcelable {

	@SerializedName("duration")
	private int duration;

	@SerializedName("icon_url")
	private String iconUrl;

	@SerializedName("rate")
	private int rate;

	@SerializedName("year")
	private int year;

	@SerializedName("director")
	private String director;

	@SerializedName("image_url")
	private String imageUrl;

	@SerializedName("starring")
	private String starring;

	@SerializedName("vod_type")
	private String vodType;

	@SerializedName("id")
	private int id;

	@SerializedName("pin_protected")
	private int pinProtected;

	@SerializedName("title")
	private String title;

	@SerializedName("description")
	private String description;

	@SerializedName("url")
	private String url;

	@SerializedName("clicks")
	private int clicks;

	@SerializedName("number_in_parent")
	private int numberInParent;

	public VodModel(){}

	protected VodModel(Parcel in) {
		duration = in.readInt();
		iconUrl = in.readString();
		rate = in.readInt();
		year = in.readInt();
		director = in.readString();
		imageUrl = in.readString();
		starring = in.readString();
		vodType = in.readString();
		id = in.readInt();
		pinProtected = in.readInt();
		title = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(duration);
		dest.writeString(iconUrl);
		dest.writeInt(rate);
		dest.writeInt(year);
		dest.writeString(director);
		dest.writeString(imageUrl);
		dest.writeString(starring);
		dest.writeString(vodType);
		dest.writeInt(id);
		dest.writeInt(pinProtected);
		dest.writeString(title);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<VodModel> CREATOR = new Creator<VodModel>() {
		@Override
		public VodModel createFromParcel(Parcel in) {
			return new VodModel(in);
		}

		@Override
		public VodModel[] newArray(int size) {
			return new VodModel[size];
		}
	};

	public void setDuration(int duration){
		this.duration = duration;
	}

	public int getDuration(){
		return duration;
	}

	public void setIconUrl(String iconUrl){
		this.iconUrl = iconUrl;
	}

	public String getIconUrl(){
		return iconUrl;
	}

	public void setRate(int rate){
		this.rate = rate;
	}

	public int getRate(){
		return rate;
	}

	public void setYear(int year){
		this.year = year;
	}

	public int getYear(){
		return year;
	}

	public void setDirector(String director){
		this.director = director;
	}

	public String getDirector(){
		return director;
	}

	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
	}

	public String getImageUrl(){
		return imageUrl;
	}

	public void setStarring(String starring){
		this.starring = starring;
	}

	public String getStarring(){
		return starring;
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

	public void setPinProtected(int pinProtected){
		this.pinProtected = pinProtected;
	}

	public int getPinProtected(){
		return pinProtected;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}

	public int getNumberInParent() {
		return numberInParent;
	}

	public void setNumberInParent(int numberInParent) {
		this.numberInParent = numberInParent;
	}
}