package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class NewPassResponse {

	@SerializedName("status_code")
	private int statusCode;

	@SerializedName("extra_data")
	private String extraData;

	@SerializedName("error_description")
	private String errorDescription;

	@SerializedName("error_code")
	private int errorCode;

	@SerializedName("response_object")
	private NewPassResponseObject responseObject;

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

	public NewPassResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(NewPassResponseObject responseObject) {
		this.responseObject = responseObject;
	}

	public void setTimestamp(int timestamp){
		this.timestamp = timestamp;
	}

	public int getTimestamp(){
		return timestamp;
	}

	@Override
	public String toString() {
		return "NewPassResponse{" +
				"statusCode=" + statusCode +
				", extraData='" + extraData + '\'' +
				", errorDescription='" + errorDescription + '\'' +
				", errorCode=" + errorCode +
				", responseObject=" + responseObject +
				", timestamp=" + timestamp +
				'}';
	}
}