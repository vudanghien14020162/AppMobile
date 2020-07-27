package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommonResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("extra_data")
    private String extraData;

    @SerializedName("error_description")
    private String errorDescription;

    @SerializedName("error_code")
    private int errorCode;

    @SerializedName("response_object")
    private List<Object> objectList;

    @SerializedName("timestamp")
    private int timestamp;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<Object> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<Object> objectList) {
        this.objectList = objectList;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}