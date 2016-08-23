package com.allNews.utils;

import org.json.JSONArray;

/**
 * Created by Vladimir on 11.11.2015.
 */
public final class ModelUtils {

    public static String[] jArrayToStringArray(JSONArray jArray)
    {
        String[] array = null;
        try {
            array = new String[jArray.length()];
            for (int i = 0; i < jArray.length(); i++)
                array[i] = jArray.getString(i);
        } catch (Exception e){
            e.printStackTrace();
        }
        return array;
    }
}
