package com.mobitv.ott.model;

import com.google.gson.annotations.SerializedName;

public class ScardModel {
    @SerializedName("contact_name") private String contact_name;
    @SerializedName("sub_type_name") private String sub_type_name;
    @SerializedName("contact_address") private String contact_address;
    @SerializedName("expired_date") private String expired_date;
    @SerializedName("status") private String status;
    @SerializedName("scard_number") private String scard_number;


    public String getExpired_date() {
        return expired_date;
    }

    public void setExpired_date(String expired_date) {
        this.expired_date = expired_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getSub_type_name() {
        return sub_type_name;
    }

    public void setSub_type_name(String sub_type_name) {
        this.sub_type_name = sub_type_name;
    }

    public String getContact_address() {
        return contact_address;
    }

    public void setContact_address(String contact_address) {
        this.contact_address = contact_address;
    }

    public String getScard_number() {
        return scard_number;
    }

    public void setScard_number(String scard_number) {
        this.scard_number = scard_number;
    }

    @Override
    public String toString() {
        return "ScardModel{" +
                "contact_name=" + contact_name +
                ", contact_address='" + contact_address + '\'' +
                ", scard_number='" + scard_number + '\'' +
                ", expired_date='" + expired_date + '\'' +
                ", sub_type_name='" + sub_type_name + '\'' +
                '}';
    }
}
