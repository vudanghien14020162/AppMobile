package com.mobitv.ott.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VodDetails {

	@SerializedName("year")
	private String year;

	@SerializedName("description")
	private String description;

	@SerializedName("category_names")
	private String categoryNames;

	@SerializedName("title")
	private String title;

	@SerializedName("duration")
	private int duration;

	@SerializedName("encryption")
	private String encryption;

	@SerializedName("rate")
	private String rate;

	@SerializedName("canWatched")
	private boolean canWatched;

	@SerializedName("default_subtitle_id")
	private int defaultSubtitleId;

	@SerializedName("TokenUrl")
	private String tokenUrl;

	@SerializedName("vod_type")
	private String vodType;

	@SerializedName("id")
	private int id;

	@SerializedName("vod_subtitles")
	private List<Object> vodSubtitles;

	@SerializedName("stream_format")
	private String streamFormat;

	@SerializedName("categoryid")
	private String categoryid;

	@SerializedName("icon_url")
	private String iconUrl;

	@SerializedName("director")
	private String director;

	@SerializedName("image_url")
	private String imageUrl;

	@SerializedName("encryption_url")
	private String encryptionUrl;

	@SerializedName("pin_protected")
	private int pinProtected;

	@SerializedName("url")
	private String url;

	@SerializedName("trailer_url")
	private String trailerUrl;

	@SerializedName("token")
	private String token;

	@SerializedName("drm_platform")
	private String drmPlatform;

	@SerializedName("vod_preview_url")
	private String vodPreviewUrl;

	@SerializedName("clicks")
	private int clicks;

	@SerializedName("content_rating")
	private int contentRating;

	@SerializedName("starring")
	private String starring;

	@SerializedName("relatedFilms")
	private List<VodDetails> relatedFilms;

	@SerializedName("isFavorited")
	private int isFavourite;

	public void setYear(String year){
		this.year = year;
	}

	public String getYear(){
		return year;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setCategoryNames(String categoryNames){
		this.categoryNames = categoryNames;
	}

	public String getCategoryNames(){
		return categoryNames;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setDuration(int duration){
		this.duration = duration;
	}

	public int getDuration(){
		return duration;
	}

	public void setEncryption(String encryption){
		this.encryption = encryption;
	}

	public String getEncryption(){
		return encryption;
	}

	public void setRate(String rate){
		this.rate = rate;
	}

	public String getRate(){
		return rate;
	}

	public void setCanWatched(boolean canWatched){
		this.canWatched = canWatched;
	}

	public boolean isCanWatched(){
		return canWatched;
	}

	public void setDefaultSubtitleId(int defaultSubtitleId){
		this.defaultSubtitleId = defaultSubtitleId;
	}

	public int getDefaultSubtitleId(){
		return defaultSubtitleId;
	}

	public void setTokenUrl(String tokenUrl){
		this.tokenUrl = tokenUrl;
	}

	public String getTokenUrl(){
		return tokenUrl;
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

	public List<Object> getVodSubtitles() {
		return vodSubtitles;
	}

	public void setVodSubtitles(List<Object> vodSubtitles) {
		this.vodSubtitles = vodSubtitles;
	}

	public void setStreamFormat(String streamFormat){
		this.streamFormat = streamFormat;
	}

	public String getStreamFormat(){
		return streamFormat;
	}

	public void setCategoryid(String categoryid){
		this.categoryid = categoryid;
	}

	public String getCategoryid(){
		return categoryid;
	}

	public void setIconUrl(String iconUrl){
		this.iconUrl = iconUrl;
	}

	public String getIconUrl(){
		return iconUrl;
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

	public void setEncryptionUrl(String encryptionUrl){
		this.encryptionUrl = encryptionUrl;
	}

	public String getEncryptionUrl(){
		return encryptionUrl;
	}

	public void setPinProtected(int pinProtected){
		this.pinProtected = pinProtected;
	}

	public int getPinProtected(){
		return pinProtected;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}

	public void setTrailerUrl(String trailerUrl){
		this.trailerUrl = trailerUrl;
	}

	public String getTrailerUrl(){
		return trailerUrl;
	}

	public void setToken(String token){
		this.token = token;
	}

	public String getToken(){
		return token;
	}

	public void setDrmPlatform(String drmPlatform){
		this.drmPlatform = drmPlatform;
	}

	public String getDrmPlatform(){
		return drmPlatform;
	}

	public void setVodPreviewUrl(String vodPreviewUrl){
		this.vodPreviewUrl = vodPreviewUrl;
	}

	public String getVodPreviewUrl(){
		return vodPreviewUrl;
	}

	public void setClicks(int clicks){
		this.clicks = clicks;
	}

	public int getClicks(){
		return clicks;
	}

	public void setStarring(String starring){
		this.starring = starring;
	}

	public String getStarring(){
		return starring;
	}

	public List<VodDetails> getRelatedFilms() {
		return relatedFilms;
	}

	public void setRelatedFilms(List<VodDetails> relatedFilms) {
		this.relatedFilms = relatedFilms;
	}

	public int getContentRating() {
		return contentRating;
	}

	public void setContentRating(int contentRating) {
		this.contentRating = contentRating;
	}

	public int getIsFavourite() {
		return isFavourite;
	}

	public void setIsFavourite(int isFavourite) {
		this.isFavourite = isFavourite;
	}
}