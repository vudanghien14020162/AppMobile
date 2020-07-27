package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class ResendOTPNewPassParams {
    @SerializedName("tel")
    private String tel;

    public ResendOTPNewPassParams(String tel) {
        this.tel = tel;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

}
