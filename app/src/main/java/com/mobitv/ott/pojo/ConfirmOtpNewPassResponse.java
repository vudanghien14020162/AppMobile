package com.mobitv.ott.pojo;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

public class ConfirmOtpNewPassResponse {

	@SerializedName("status_code")
	private int statusCode;

	@SerializedName("extra_data")
	private String extraData;

	@SerializedName("error_description")
	private String errorDescription;

	@SerializedName("error_code")
	private int errorCode;

	@SerializedName("response_object")
	private ConfirmOtpNewPassResponseObject responseObject;

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

	public void setResponseObject(ConfirmOtpNewPassResponseObject responseObject){
		this.responseObject = responseObject;
	}

	public ConfirmOtpNewPassResponseObject getResponseObject(){
		return responseObject;
	}

	public void setTimestamp(int timestamp){
		this.timestamp = timestamp;
	}

	public int getTimestamp(){
		return timestamp;
	}

	@Override
	@NonNull
	public String toString() {
		return "ConfirmOtpNewPassResponseObject{" +
				"statusCode=" + statusCode +
				", extraData='" + extraData + '\'' +
				", errorDescription='" + errorDescription + '\'' +
				", errorCode=" + errorCode +
				", responseObject=" + responseObject +
				", timestamp=" + timestamp +
				'}';
	}
}