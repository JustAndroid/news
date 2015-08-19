package com.allNews.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_history")
public class History {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField()
	private int newsId;

	@DatabaseField()
	private String time;
	
	@DatabaseField()
	private boolean isSynch;

	@DatabaseField
	private int isLike;
	@DatabaseField
	private int isDisLike;

	public History() {

	}

	public int getIsLike() {
		return isLike;
	}

	public void setIsLike(int isLike) {
		this.isLike = isLike;
	}

	public int getIsDisLike() {
		return isDisLike;
	}

	public void setIsDisLike(int isDisLike) {
		this.isDisLike = isDisLike;
	}

	public int getNewsID() {
		return newsId;
	}

	public void setNewsID(int newsId) {
		this.newsId = newsId;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	 
}
