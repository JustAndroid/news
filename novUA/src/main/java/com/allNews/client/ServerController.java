package com.allNews.client;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.allNews.common.ServerConstants;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Vladimir on 11.11.2015.
 */
public class ServerController {

    private static final String TAG = "ServerController";

    private static RequestQueue queue;

    public static void initializeVolley(Context context)
    {
        queue = Volley.newRequestQueue(context);
    }

    public static RequestQueue Volley(Context context)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);
        return queue;
    }

    public static void getClientInfo(Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);

        Log.e(TAG, ServerConstants.SERVER + ServerConstants.GET_CLIENT_INFO);

        JsonObjectRequest clientInfo = new JsonObjectRequest(
                Request.Method.GET
                , ServerConstants.SERVER + ServerConstants.GET_CLIENT_INFO
                , listener
                , errorListener);
        clientInfo.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(clientInfo);
    }

    public static void getArticles(Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);

        JsonObjectRequest articles = new JsonObjectRequest(
                Request.Method.GET
                , ServerConstants.SERVER + ServerConstants.GET_ARTICLES
                , listener
                , errorListener);

        articles.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(articles);
    }

    public static void getArticlesAfterID(Context context, String ID, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);

        JsonObjectRequest articlesAfterID = new JsonObjectRequest(
                Request.Method.GET
                , ServerConstants.SERVER + ServerConstants.GET_ARTICLES_AFTER_ARTICLE_ID + ID
                , listener
                , errorListener);

        articlesAfterID.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(articlesAfterID);
    }

    public static void getSourcesInfo(Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);

        JsonObjectRequest sourcesInfo = new JsonObjectRequest(
                Request.Method.GET
                , ServerConstants.SERVER + ServerConstants.GET_SOURCES_INFO
                , listener
                , errorListener);

        sourcesInfo.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sourcesInfo);
    }

    public static void getArticlesBySourcesID(Context context, String[] IDs, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);

        String ids = "";
        if (IDs != null)
            for (int i = 0; i < IDs.length; i++)
                ids += IDs[i] + (i<(IDs.length-1)?"%2C":"");

        JsonObjectRequest articlesBySourcesID = new JsonObjectRequest(
                Request.Method.GET
                , ServerConstants.SERVER + ServerConstants.GET_ARTICLES_BY_SOURCE_ID + ids
                , listener
                , errorListener);

        articlesBySourcesID.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(articlesBySourcesID);
    }

    public static void getArticlesAfterIDBySourcesID(Context context, String articleID, String[] IDs, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);

        String ids = "";
        if (IDs != null)
            for (int i = 0; i < IDs.length; i++)
                ids += IDs[i] + (i<(IDs.length-1)?"%2C":"");

        JsonObjectRequest articlesBySourcesID = new JsonObjectRequest(
                Request.Method.GET
                , ServerConstants.SERVER + ServerConstants.GET_ARTICLES_AFTER_ARTICLE_ID_BY_SOURCES_ID + ids + (ids.length()>0?"/":"") + articleID
                , listener
                , errorListener);

        articlesBySourcesID.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(articlesBySourcesID);
    }



    public static void postFirstLaunch(Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ServerConstants.CLIENT, ServerConstants.CLIENT_ID);
            jsonObject.put(ServerConstants.OS, "1");
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                jsonObject.put(ServerConstants.VERSION, pInfo.versionName);
            } catch (PackageManager.NameNotFoundException nameNotFoundException){
                nameNotFoundException.printStackTrace();
            }
        } catch (JSONException jsonException){
            jsonException.printStackTrace();
        }

        JsonObjectRequest firstLaunch = new JsonObjectRequest(
                Request.Method.POST
                , ServerConstants.SERVER + ServerConstants.POST_FIRST_LAUNCH
                , jsonObject
                , listener
                , errorListener);

        firstLaunch.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(firstLaunch);
    }

    public static void postArticleLike(Context context, String articleID, boolean like, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ServerConstants.CLIENT, ServerConstants.CLIENT_ID);
            jsonObject.put(ServerConstants.ARTICLE, articleID);
            jsonObject.put(ServerConstants.LIKE, like);
        } catch (JSONException jsonException){
            jsonException.printStackTrace();
        }

        JsonObjectRequest articleLike = new JsonObjectRequest(
                Request.Method.POST
                , ServerConstants.SERVER + ServerConstants.POST_ARTICLE_LIKE
                , jsonObject
                , listener
                , errorListener);

        articleLike.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(articleLike);
    }

    public static void postArticleWasRead(Context context, String articleID, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ServerConstants.CLIENT, ServerConstants.CLIENT_ID);
            jsonObject.put(ServerConstants.ARTICLE, articleID);
        } catch (JSONException jsonException){
            jsonException.printStackTrace();
        }

        JsonObjectRequest articleWasRead = new JsonObjectRequest(
                Request.Method.POST
                , ServerConstants.SERVER + ServerConstants.POST_ARTICLE_WAS_READ
                , jsonObject
                , listener
                , errorListener);

        articleWasRead.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(articleWasRead);
    }




    public static void getEvents(Context context, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_id", ServerConstants.CLIENT_ID);
        } catch (JSONException jsonException){
            jsonException.printStackTrace();
        }

        JsonArrayRequest events = new JsonArrayRequest(
                Request.Method.POST
                , ServerConstants.GET_EVENTS
                , jsonObject
                , listener
                , errorListener) {
            @Override
            public HashMap<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("charset", "UTF-8");
                return params;
            }

        };

        events.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(events);
    }
}
