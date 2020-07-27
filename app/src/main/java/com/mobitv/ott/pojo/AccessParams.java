package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class AccessParams {
    @SerializedName("PackageID")
    private String packageID;

    public AccessParams(String packageID) {
        this.packageID = packageID;
    }

}
