package com.allNews.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vladimir on 12.11.2015.
 */
public class Event extends Article {

//    "event_id":23723,
//    "name_ua":null,
//    "name_ru":"NLPt Psychotherapy Diploma",
//    "name_en":null,
//    "description":"",
//    "when":"1397671200",
//    "when_end":"1449847800",
//    "city":"",
//    "country":"\u0423\u043a\u0440\u0430\u0438\u043d\u0430",
//    "category":"Tech & IT",
//    "category_id":2,
//    "where":"",
//    "address":"",
//    "map":{"lat":"55.741668","lng":"37.566008","zooms":"13"},
//    "eventpage":"http:\/\/nlpt-psychotherapy-diploma.2event.com",
//    "poster_small":"http:\/\/api2.withmyfriends.org\/media\/events\/2014\/06\/1560-index10.jpg",
//    "poster_middle":"http:\/\/api2.withmyfriends.org\/media\/events\/2014\/06\/6458-index10-middle.jpg",
//    "poster_big":"http:\/\/api2.withmyfriends.org\/media\/events\/2014\/06\/6458-index10.jpg",
//    "price_min":0,
//    "currency":null,
//    "is_free":false,
//    "is_discount":false,
//    "is_top":false

    private String eventID;
    private String nameUA;
    private String nameRU;
    private String nameEN;
    private String description;
    private String dateStart;
    private String dateEnd;
    private String city;
    private String country;
    private String category;
    private int categoryID;
    private String where;
    private String address;
    private String map;         //JSONObject ( double "lat", double "lng", String "zooms" )
    private String eventPage;
    private String posterSmall;
    private String posterMiddle;
    private String posterBig;
    private int priceMin;
    private String currency;
    private boolean isFree;
    private boolean isDiscount;
    private boolean isTop;

    public Event(String eventID, String nameUA, String nameRU, String nameEN, String description,
                 String dateStart, String dateEnd, String city, String country, String category,
                 int categoryID, String where, String address, String map, String eventPage,
                 String posterSmall, String posterMiddle, String posterBig, int priceMin,
                 String currency, boolean isFree, boolean isDiscount, boolean isTop)
    {
        super(true);

        this.eventID = eventID;
        this.nameUA = nameUA;
        this.nameRU = nameRU;
        this.nameEN = nameEN;
        this.description = description;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.city = city;
        this.country = country;
        this.category = category;
        this.categoryID = categoryID;
        this.where = where;
        this.address = address;
        this.map = map;
        this.eventPage = eventPage;
        this.posterSmall = posterSmall;
        this.posterMiddle = posterMiddle;
        this.posterBig = posterBig;
        this.priceMin = priceMin;
        this.currency = currency;
        this.isFree = isFree;
        this.isDiscount = isDiscount;
        this.isTop = isTop;
    }

    public Event(JSONObject object) throws JSONException
    {
        super(true);

        this.eventID = object.getString("event_id");
        this.nameUA = object.getString("name_ua");
        this.nameRU = object.getString("name_ru");
        this.nameEN = object.getString("name_en");
        this.description = object.getString("description");
        this.dateStart = object.getString("when");
        this.dateEnd = object.getString("when_end");
        this.city = object.getString("city");
        this.country = object.getString("country");
        this.category = object.getString("category");
        this.categoryID = object.getInt("category_id");
        this.where = object.getString("where");
        this.address = object.getString("address");
        this.map = object.getString("map");
        this.eventPage = object.getString("eventpage");
        this.posterSmall = object.getString("poster_small");
        this.posterMiddle = object.getString("poster_middle");
        this.posterBig = object.getString("poster_big");
        this.priceMin = object.getInt("price_min");
        this.currency = object.getString("price_min");
        this.isFree = object.getBoolean("is_free");
        this.isDiscount = object.getBoolean("is_discount");
        this.isTop = object.getBoolean("is_top");
    }

    public String getEventID(){
        return eventID;
    }
    public String getNameUA(){
        return nameUA;
    }
    public String getNameRU(){
        return nameRU;
    }
    public String getNameEN(){
        return nameEN;
    }
    public String getDescription(){
        return description;
    }
    public String getDateStart(){
        return dateStart;
    }
    public String getDateEnd(){
        return dateEnd;
    }
    public String getCity(){
        return city;
    }
    public String getCountry(){
        return country;
    }
    public String getCategory(){
        return category;
    }
    public int getCategoryID(){
        return categoryID;
    }
    public String getWhere(){
        return where;
    }
    public String getAddress(){
        return address;
    }
    public String getMap(){
        return map;
    }
    public String getEventPage(){
        return eventPage;
    }
    public String getPosterSmall(){
        return posterSmall;
    }
    public String getPosterMiddle(){
        return posterMiddle;
    }
    public String getPosterBig(){
        return posterBig;
    }
    public int getPriceMin(){
        return priceMin;
    }
    public String getCurrency(){
        return currency;
    }
    public boolean isFree(){
        return isFree;
    }
    public boolean isDiscount(){
        return isDiscount;
    }
    public boolean isTop(){
        return isTop;
    }

    public double getLatitude(){
        try {
            return new JSONObject(map).getDouble("lat");
        } catch (Exception e){
            return 0;
        }
    }
    public double getLongitude(){
        try {
            return new JSONObject(map).getDouble("lng");
        } catch (Exception e){
            return 0;
        }
    }
    public int getZoom(){
        try {
            return new JSONObject(map).getInt("zooms");
        } catch (Exception e){
            return 0;
        }
    }
}
