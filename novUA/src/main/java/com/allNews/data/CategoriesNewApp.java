package com.allNews.data;

import com.allNews.db.DBOpenerHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_categories_newapp")
public class CategoriesNewApp {

	@DatabaseField(generatedId = true)
	private int idIndex;

	@Expose(serialize = true)
	@DatabaseField()
	private int id;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_CATEGORIES_EN)
	@DatabaseField()
	private String en;
 

	public CategoriesNewApp() {
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return en;
	}
 

}
