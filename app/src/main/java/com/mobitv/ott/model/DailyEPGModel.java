package com.mobitv.ott.model;

import com.google.gson.annotations.SerializedName;

public class DailyEPGModel{

	@SerializedName("programstart")
	private String programstart;

	@SerializedName("duration")
	private int duration;

	@SerializedName("number")
	private String number;

	@SerializedName("scheduled")
	private boolean scheduled;

	@SerializedName("programend")
	private String programend;

	@SerializedName("description")
	private String description;

	@SerializedName("progress")
	private int progress;

	@SerializedName("channelName")
	private String channelName;

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

	@SerializedName("shortname")
	private String shortname;

	@SerializedName("status")
	private int status;
	@SerializedName("canWatched")
	private boolean canWatched;

	private boolean isPlaying;

	private boolean isDisable;

	public void setProgramstart(String programstart){
		this.programstart = programstart;
	}

	public String getProgramstart(){
		return programstart;
	}

	public void setDuration(int duration){
		this.duration = duration;
	}

	public int getDuration(){
		return duration;
	}

	public void setNumber(String number){
		this.number = number;
	}

	public String getNumber(){
		return number;
	}

	public void setScheduled(boolean scheduled){
		this.scheduled = scheduled;
	}

	public boolean isScheduled(){
		return scheduled;
	}

	public void setProgramend(String programend){
		this.programend = programend;
	}

	public String getProgramend(){
		return programend;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setProgress(int progress){
		this.progress = progress;
	}

	public int getProgress(){
		return progress;
	}

	public void setChannelName(String channelName){
		this.channelName = channelName;
	}

	public String getChannelName(){
		return channelName;
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

	public void setShortname(String shortname){
		this.shortname = shortname;
	}

	public String getShortname(){
		return shortname;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean playing) {
		isPlaying = playing;
	}

	public boolean isDisable() {
		return isDisable;
	}

	public void setDisable(boolean disable) {
		isDisable = disable;
	}

	public boolean isCanWatched() {
		return canWatched;
	}

	public void setCanWatched(boolean canWatched) {
		this.canWatched = canWatched;
	}
}