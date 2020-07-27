package com.mobitv.ott.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VodCategoryModel {

	@SerializedName("IconUrl")
	private String iconUrl;

	@SerializedName("small_icon_url")
	private String smallIconUrl;

	@SerializedName("password")
	private String password;

	@SerializedName("sorting")
	private int sorting;

	@SerializedName("name")
	private String name;

	@SerializedName("pay")
	private String pay;

	@SerializedName("id")
	private String id;

	@SerializedName("base_url")
	private String baseURL;

	@SerializedName("listFilm")
	private ArrayList<VodModel> listFilm;

	public void setIconUrl(String iconUrl){
		this.iconUrl = iconUrl;
	}

	public String getIconUrl(){
		return iconUrl;
	}

	public void setSmallIconUrl(String smallIconUrl){
		this.smallIconUrl = smallIconUrl;
	}

	public String getSmallIconUrl(){
		return smallIconUrl;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}

	public void setSorting(int sorting){
		this.sorting = sorting;
	}

	public int getSorting(){
		return sorting;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setPay(String pay){
		this.pay = pay;
	}

	public String getPay(){
		return pay;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setListFilm(ArrayList<VodModel> listFilm){
		this.listFilm = listFilm;
	}

	public ArrayList<VodModel> getListFilm(){
		return listFilm;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBassURL(String baseURL) {
		this.baseURL = baseURL;
	}
}