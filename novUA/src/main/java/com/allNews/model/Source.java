package com.allNews.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vladimir on 11.11.2015.
 */
public class Source {

//    "id": 4,
//    "name": "Кореспондент (RU)",
//    "url": "http://korrespondent.net/",
//    "title": "Последние новости на сайте korrespondent.net",
//    "language": "Російська",
//    "language_code": "ru",
//    "is_category": 0,
//    "default": 0
//    **************************************************
//    id (string),
//    name (string),
//    url (string),
//    title (string),
//    language (string),
//    language_code (string),
//    is_category (integer),
//    default (integer)

    private String sourceID;
    private String name;
    private String url;
    private String title;
    private String language;
    private String language_code;
    private int isCategory;
    private int isDefault;

    public Source(String sourceID, String name, String url, String title
            , String language, String language_code, int isCategory, int isDefault)
    {
        this.sourceID = sourceID;
        this.name = name;
        this.url = url;
        this.title = title;
        this.language = language;
        this.language_code = language_code;
        this.isCategory = isCategory;
        this.isDefault = isDefault;
    }

    public Source(JSONObject object) throws JSONException
    {
        this.sourceID = object.getString("id");
        this.name = object.getString("name");
        this.url = object.getString("url");
        this.title = object.getString("title");
        this.language = object.getString("language");
        this.language_code = object.getString("language_code");
        this.isCategory = object.getInt("is_category");
        this.isDefault = object.getInt("default");
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Source && ((Source) o).getSourceID() != null && ((Source) o).getSourceID().equals(sourceID));
    }

    @Override
    public int hashCode() {
        return (sourceID!=null?Integer.parseInt(sourceID):0);
    }

    public String getSourceID(){
        return sourceID;
    }
    public String getName(){
        return name;
    }
    public String getUrl(){
        return url;
    }
    public String getTitle(){
        return title;
    }
    public String getLanguage(){
        return language;
    }
    public String getLanguage_code(){
        return language_code;
    }
    public int getIsCategory(){
        return isCategory;
    }
    public int getIsDefault(){
        return isDefault;
    }
}
