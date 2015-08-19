package com.allNews.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.allNews.activity.Preferences;
import com.allNews.db.DBOpenerHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_categories")
public class Categories {
	
	@DatabaseField(generatedId = true)
	private int idIndex;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_CATEGORIES_ID)
	@DatabaseField()
	private int id;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_CATEGORIES_RU)
	@DatabaseField()
	private String ru;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_CATEGORIES_EN)
	@DatabaseField()
	private String en;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_CATEGORIES_UA)
	@DatabaseField()
	private String ua;

	@DatabaseField()
	private int isActive;
	
	public Categories() {

	}

	public String getEn() {
		return en;
	}

	public void setEn(String name) {
		this.en = name;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String name) {
		this.ua = name;
	}

	public String getRu() {
		return ru;
	}

	public void setRu(String name) {
		this.ru = name;
	}

	public int getCategoryID() {
		return id;
	}

	public void setCategoryID(int CategoriesID) {
		this.id = CategoriesID;
	}
	

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getCategoryName(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String lang = sp.getString(Preferences.PREF_LANGUAGE, "ru");

		if (lang.equals(Preferences.LANGUAGE_UA) && ua != null && !ua.equals(""))
			return ua;
		else if (lang.equals(Preferences.LANGUAGE_EN) && en != null && !en.equals(""))
			return en;
		else if (ru != null)
			return ru;
		return "";

	}
   
}
