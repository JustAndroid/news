package com.allNews.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.allNews.activity.Preferences;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_events")
public class Event {

	@Expose(serialize = false)
	@DatabaseField(generatedId = true)
	int id;

	@Expose(serialize = true)
	@SerializedName("event_id")
	@DatabaseField(columnName = "event_id")
	private long eventId;

	@Expose(serialize = true)
	@SerializedName("name_ua")
	@DatabaseField(columnName = "name_ua")
	private String name_ua;

	@Expose(serialize = true)
	@SerializedName("name_ru")
	@DatabaseField(columnName = "name_ru")
	private String name_ru;

	@Expose(serialize = true)
	@SerializedName("name_en")
	@DatabaseField(columnName = "name_en")
	private String name_en;

	@Expose(serialize = false)
	@DatabaseField(columnName = "name_ua_index")
	private String name_ua_index;

	@Expose(serialize = false)
	@DatabaseField(columnName = "name_ru_index")
	private String name_ru_index;
	@Expose(serialize = false)
	@DatabaseField(columnName = "name_en_index")
	private String name_en_index;
	
	@Expose(serialize = true)
	@SerializedName("city")
	@DatabaseField(columnName = "city")
	private String city;

	@Expose(serialize = false)
	@DatabaseField(columnName = "cityIndex")
	private String cityIndex;
	
	@Expose(serialize = true)
	@SerializedName("when")
	@DatabaseField(columnName = "whenStart")
	private long startDate;

	@Expose(serialize = true)
	@SerializedName("description")
	@DatabaseField(columnName = "description")
	private String description;

	@Expose(serialize = true)
	@SerializedName("address")
	@DatabaseField(columnName = "address")
	private String address;

	@Expose(serialize = true)
	@SerializedName("where")
	@DatabaseField(columnName = "where")
	private String where;

	@Expose(serialize = true)
	@SerializedName("poster_small")
	@DatabaseField(columnName = "poster_small")
	private String logoSmallUrl;

	@Expose(serialize = true)
	@SerializedName("poster_middle")
	@DatabaseField(columnName = "poster_middle")
	private String logoMiddleUrl;

	@Expose(serialize = true)
	@SerializedName("poster_big")
	@DatabaseField(columnName = "poster_big")
	private String logoBigUrl;
	
	@Expose(serialize = true)
	@SerializedName("eventpage")
	@DatabaseField(columnName = "eventpage")
	private String pageUrl;

	@Expose(serialize = true)
	@DatabaseField(columnName = "country")
	private String country;

	@Expose(serialize = true)
	@SerializedName("category")
	@DatabaseField(columnName = "category")
	private String category;
	
	@Expose(serialize = true)
	@SerializedName("is_top")
	@DatabaseField(columnName = "is_top")
	private boolean isTop;

	@Expose(serialize = true)
	@SerializedName("is_discount")
	@DatabaseField(columnName = "is_discount")
	private boolean discount;
	
	@Expose(serialize = true)
	@SerializedName("is_free")
	@DatabaseField(columnName = "is_free")
	private boolean is_free;
	
	@Expose(serialize = true)
	@SerializedName("map")
	private Map map;

	@Expose(serialize = false)
	@DatabaseField(columnName = "lat")
	private double mapLat;

	@Expose(serialize = false)
	@DatabaseField(columnName = "lng")
	private double mapLng;

	@Expose(serialize = true)
	@DatabaseField(columnName = "price_min")
	@SerializedName("price_min")
	private float price_min;

	@Expose(serialize = true)
	@SerializedName("currency")
	@DatabaseField(columnName = "currency")
	private String currency;
	
	public Event() {

		map = new Map();
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public String getCategory() { 
		return category;
	}
	
	public String getNameEn() {
		return name_en;
	}

	public String getNameRu() {
		return name_ru;
	}

	public String getNameUa() {
		return name_ua;
	}
	public void setNameUaIndex(String name) {
		this.name_ua_index = name;
	}
	
	public void setNameRuIndex(String name) {
		this.name_ru_index = name;
	}
	
	public void setNameEnIndex(String name) {
		this.name_en_index = name;
	}
	
	public String getName(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String lang = sp.getString(Preferences.PREF_LANGUAGE, "ru");

		if (lang.equals(Preferences.LANGUAGE_UA) && name_ua != null && !name_ua.equals(""))
			return name_ua;
		else if (lang.equals(Preferences.LANGUAGE_EN) && name_en != null && !name_en.equals(""))
			return name_en;
		else if (name_ru != null)
			return name_ru;
		return "";

	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityIndex() {
		return cityIndex;
	}

	public void setCityIndex(String cityIndex) {
		this.cityIndex = cityIndex;
	}
	
	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public String getLogoSmallUrl() {
		return logoSmallUrl;
	}

	public void setLogoSmallUrl(String logoSmallUrl) {
		this.logoSmallUrl = logoSmallUrl;
	}

	public String getLogoBigUrl() {
		return logoBigUrl;
	}

	public void setLogoBigUrl(String logoBigUrl) {
		this.logoBigUrl = logoBigUrl;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public boolean isTop() {
		return isTop;
	}
	
	public boolean isFree() {
		return is_free;
	}
	
	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	public boolean isDiscount() {
		return discount;
	}

	public void setDiscount(boolean discount) {
		this.discount = discount;
	}

	public double getMapLat() {
		return mapLat;
	}

	public void setMapLat(double mapLat) {
		this.mapLat = mapLat;
	}

	public double getMapLng() {
		return mapLng;
	}

	public void setMapLng(double mapLng) {
		this.mapLng = mapLng;
	}

	public float getPrice() {
		return price_min;
	}

	public void setPrice(float price_min) {
		this.price_min = price_min;
	}
	public String getCurrency() {
		return currency;
	}
	public class Map {
		@Expose(serialize = true)
		@SerializedName("lat")
		private double mapLat;

		@Expose(serialize = true)
		@SerializedName("lng")
		private double mapLng;

		public double getMapLat() {
			return mapLat;
		}

		public void setMapLat(double mapLat) {
			this.mapLat = mapLat;
		}

		public double getMapLng() {
			return mapLng;
		}

		public void setMapLng(double mapLng) {
			this.mapLng = mapLng;
		}

	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
