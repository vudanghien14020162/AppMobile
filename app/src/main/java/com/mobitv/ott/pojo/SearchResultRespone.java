package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;
import com.mobitv.ott.model.SearchResultCategoryModel;

import java.util.List;

public class SearchResultRespone{

	@SerializedName("status_code")
	private int statusCode;

	@SerializedName("extra_data")
	private String extraData;

	@SerializedName("error_description")
	private String errorDescription;

	@SerializedName("error_code")
	private int errorCode;

	@SerializedName("response_object")
	private List<SearchResultCategoryModel> responseObject;

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

	public void setResponseObject(List<SearchResultCategoryModel> responseObject){
		this.responseObject = responseObject;
	}

	public List<SearchResultCategoryModel> getResponseObject(){
		return responseObject;
	}

	public void setTimestamp(int timestamp){
		this.timestamp = timestamp;
	}

	public int getTimestamp(){
		return timestamp;
	}
}