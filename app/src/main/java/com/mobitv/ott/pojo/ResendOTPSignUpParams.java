package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class ResendOTPSignUpParams {
    @SerializedName("tel")
    private String tel;

    public ResendOTPSignUpParams(String tel) {
        this.tel = tel;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

}
