package com.allNews.data;

import com.allNews.db.DBOpenerHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_sources")
public class Source {

	@DatabaseField(generatedId = true)
	private int idIndex;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_SOURCE_ID)
	@DatabaseField()
	private int id;

	@Expose(serialize = true)
	@SerializedName("full")
	@DatabaseField(columnName=DBOpenerHelper.KEY_SOURCE_ENABLED)
	private int enabled;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_SOURCE_NAME)
	@DatabaseField()
	private String name;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_SOURCE_APP)
	@DatabaseField()
	private String app;

	@Expose(serialize = false)
	@DatabaseField()
	private int isActive;

	@Expose(serialize = true)
	@SerializedName("default")
	@DatabaseField(columnName = DBOpenerHelper.KEY_SOURCE_DEFAULT)
	private int defaultSource;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_SOURCE_URL)
	@DatabaseField()
	private String url;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_SOURCE_IMAGE_URL)
	@DatabaseField()
	private String imageUrl;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_SOURCE_LANGUAGE)
	@DatabaseField()
	private String language;
 

	public Source() {

	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public int getDefault() {
		return defaultSource;
	}

	public void setDefault(int defaultSource) {
		this.defaultSource = defaultSource;

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;

	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;

	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int getSourceID() {
		return id;
	}

	public void setSourceID(int sourceID) {
		this.id = sourceID;
	}

 

}
