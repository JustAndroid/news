package com.allNews.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_update")
public class Update {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField()
	private String updateUrl;

	@DatabaseField()
	private long startTime;

	@DatabaseField()
	private long endTime;

	public Update() {

	}

	public Update(String url, long beginTime, long endTime) {
		setUpdateUrl(url);
		setStartTime(beginTime);
		setEndTime(endTime);
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public String setUpdateUrl(String updateUrl) {
		return this.updateUrl = updateUrl;
	}

	public void setStartTime(long time) {
		this.startTime = time;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setEndTime(long time) {
		this.endTime = time;
	}

	public long getEndTime() {
		return endTime;
	}
}
