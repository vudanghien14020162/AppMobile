package com.mobitv.ott.model;

import com.google.gson.annotations.SerializedName;
import com.mobitv.ott.pojo.LoginDatum;

import java.util.List;

public class CommentModel {


	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("root")
	private List<CommentModel> root;

	@SerializedName("vod_id")
	private int vodId;

	@SerializedName("login_datum")
	private LoginDatum loginDatum;

	@SerializedName("root_id")
	private Object rootId;

	@SerializedName("id")
	private int id;

	@SerializedName("content")
	private String content;

	private int level;

	private boolean isCollapsed = true;

	private boolean isCollapseStateChange;

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setRoot(List<CommentModel> root){
		this.root = root;
	}

	public List<CommentModel> getRoot(){
		return root;
	}

	public void setVodId(int vodId){
		this.vodId = vodId;
	}

	public int getVodId(){
		return vodId;
	}

	public void setLoginDatum(LoginDatum loginDatum){
		this.loginDatum = loginDatum;
	}

	public LoginDatum getLoginDatum(){
		return loginDatum;
	}

	public void setRootId(Object rootId){
		this.rootId = rootId;
	}

	public Object getRootId(){
		return rootId;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return content;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isCollapsed() {
		return isCollapsed;
	}

	public void setCollapsed(boolean collapsed) {
		isCollapsed = collapsed;
	}

	public boolean isCollapseStateChange() {
		return isCollapseStateChange;
	}

	public void setCollapseStateChange(boolean collapseStateChange) {
		isCollapseStateChange = collapseStateChange;
	}
}