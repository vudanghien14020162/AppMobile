package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;

public class LoginDatum{

	@SerializedName("customer_datum")
	private CustomerDatum customerDatum;

	@SerializedName("id")
	private int id;

	@SerializedName("tel")
	private String tel;

	public void setCustomerDatum(CustomerDatum customerDatum){
		this.customerDatum = customerDatum;
	}

	public CustomerDatum getCustomerDatum(){
		return customerDatum;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
}