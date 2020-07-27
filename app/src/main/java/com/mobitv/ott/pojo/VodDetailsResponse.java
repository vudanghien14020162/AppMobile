package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;
import com.mobitv.ott.model.VodDetails;

public class VodDetailsResponse{

	@SerializedName("status_code")
	private int statusCode;

	@SerializedName("extra_data")
	private String extraData;

	@SerializedName("error_description")
	private String errorDescription;

	@SerializedName("error_code")
	private int errorCode;

	@SerializedName("response_object")
	private VodDetails vodDetails;

	@SerializedName("timestamp")
	private int timestamp;

	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}

	public int getStatusCode(){
		return statusCode;
	}

	public void setExtraData(String extraData){
		this.extraData = extraData;
	}

	public String getExtraData(){
		return extraData;
	}

	public void setErrorDescription(String errorDescription){
		this.errorDescription = errorDescription;
	}

	public String getErrorDescription(){
		return errorDescription;
	}

	public void setErrorCode(int errorCode){
		this.errorCode = errorCode;
	}

	public int getErrorCode(){
		return errorCode;
	}

	public void setVodDetails(VodDetails vodDetails){
		this.vodDetails = vodDetails;
	}

	public VodDetails getVodDetails(){
		return vodDetails;
	}

	public void setTimestamp(int timestamp){
		this.timestamp = timestamp;
	}

	public int getTimestamp(){
		return timestamp;
	}

	@Override
	public String toString() {
		return "VodDetailsResponse{" +
				"statusCode=" + statusCode +
				", extraData='" + extraData + '\'' +
				", errorDescription='" + errorDescription + '\'' +
				", errorCode=" + errorCode +
				", vodDetails=" + vodDetails +
				", timestamp=" + timestamp +
				'}';
	}
}