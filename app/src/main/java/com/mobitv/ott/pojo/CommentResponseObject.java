package com.mobitv.ott.pojo;

import com.google.gson.annotations.SerializedName;
import com.mobitv.ott.model.CommentModel;

import java.util.List;

public class CommentResponseObject {
	@SerializedName("listComment")
	private List<CommentModel> list;

	@SerializedName("count")
	private int count;

	public List<CommentModel> getList() {
		return list;
	}

	public void setList(List<CommentModel> list) {
		this.list = list;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}