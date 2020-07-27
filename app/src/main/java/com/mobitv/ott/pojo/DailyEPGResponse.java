package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;
import com.mobitv.ott.model.DailyEPGModel;
import com.mobitv.ott.model.LiveTvCategoryModel;

import java.util.List;

public class DailyEPGResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("extra_data")
    private String extraData;

    @SerializedName("error_description")
    private String errorDescription;

    @SerializedName("error_code")
    private int errorCode;

    @SerializedName("response_object")
    private List<DailyEPGModel> listModel;

    @SerializedName("timestamp")
    private int timestamp;

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public List<DailyEPGModel> getListModel() {
        return listModel;
    }

    public void setListModel(List<DailyEPGModel> listModel) {
        this.listModel = listModel;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "DailyEPGResponse{" +
                "statusCode=" + statusCode +
                ", extraData='" + extraData + '\'' +
                ", errorDescription='" + errorDescription + '\'' +
                ", errorCode=" + errorCode +
                ", listModel=" + listModel +
                ", timestamp=" + timestamp +
                '}';
    }
}