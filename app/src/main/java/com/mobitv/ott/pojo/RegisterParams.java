package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class RegisterParams {
    @SerializedName("tel")
    private String tel;
    @SerializedName("password")
    private String password;

    public RegisterParams(String tel, String password) {
        this.tel = tel;
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
