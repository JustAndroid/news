package com.allNews.managers;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.allNews.data.TaxonomyNewApp;
import com.allNews.db.DatabaseHelper;
import com.allNews.utils.GsonUtils;
import com.allNews.web.Requests;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;

public class ManagerTaxonomyNewApp {

    public static JsonArrayRequest getTaxonomyNewAppRequest(final Context context,
                                                            final Handler handler) {
        String url = context.getResources().getString(R.string.url_taxonomy_new_app);


        return Requests.getRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                List<TaxonomyNewApp> result = new ArrayList<TaxonomyNewApp>();
                Type listType = new TypeToken<List<TaxonomyNewApp>>() {
                }.getType();
                try {

                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);
                //  Log.e("result.get(0).getName()" ,result.get(0).getTagID() );
                    saveTaxonomyNewAppInThread(context, result, handler);

                } catch (Exception e) {
                    handler.sendEmptyMessage(1);
                    Log.e("getTaxonomyRequest", e.toString());
                }

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("ManagerNews error", "" + error);
                if (handler != null)
                    handler.sendEmptyMessage(2);
            }
        });
    }

    private static void saveTaxonomyNewAppInThread(final Context context, final List<TaxonomyNewApp> result, final Handler handler) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Log.d("getNewAppRequestNewApi", "saveInThread");

                    saveTaxonomyNewApp(context, result, handler);

                } catch (SQLException e) {

                    e.printStackTrace();
                } finally {
                    if (handler != null)
                        handler.sendEmptyMessage(1);

                }
            }

        }).start();
    }

    private static void saveTaxonomyNewApp(Context context, List<TaxonomyNewApp> result, final Handler handler) throws SQLException {
        Dao<TaxonomyNewApp, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getTaxonomyNewAppsDao();
        int size = result != null ? result.size() : 0;
        for (int index = 0; index < size; index++) {
            TaxonomyNewApp taxonomyNewApp = result.get(index);

            try {
                dao.createIfNotExists(taxonomyNewApp);

            }catch (Exception e){
                Log.e ("TaxonomySaveError", e.toString());
            }


        }

    }
    public static TaxonomyNewApp getTaxonomyNewAppFromDb(Context context, int id) {
        try {
            Dao<TaxonomyNewApp, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getTaxonomyNewAppsDao();
            return dao.queryBuilder().where().eq("tagID", id)
                    .queryForFirst();

        } catch (SQLException e) {
            return null;

        }
    }


}

