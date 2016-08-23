package com.allNews.model;

import com.allNews.utils.ModelUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vladimir on 11.11.2015.
 */
public class Customer {

//    "id": 1,
//    "name": "Кореспондент",
//    "url": "http://korrespondent.net/",
//    "include": "",
//    "feeds": [
//    1,
//    4
//    ]
//    **************************************************
//    id (string),
//    name (string),
//    url (string),
//    include (string),
//    feeds (array[string])

    private String clientID;
    private String name;
    private String url;
    private String include;
    private String[] sourcesID;

    public Customer(String clientID, String name, String url, String include, String[] sourcesID)
    {
        this.clientID = clientID;
        this.name = name;
        this.url = url;
        this.include = include;
        this.sourcesID = sourcesID;
    }
    public Customer(JSONObject object) throws JSONException
    {
        this.clientID = object.getString("id");
        this.name = object.getString("name");
        this.url = object.getString("url");
        this.include = object.getString("include");
        this.sourcesID = ModelUtils.jArrayToStringArray(object.getJSONArray("feeds"));
    }


    public String getClientID(){
        return clientID;
    }
    public String getName(){
        return name;
    }
    public String getUrl(){
        return url;
    }
    public String getInclude(){
        return include;
    }
    public String[] getSourcesID(){
        return sourcesID;
    }
}
