package com.allNews.managers;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.allNews.data.UpdateNewApp;
import com.allNews.db.DatabaseHelper;
import com.allNews.utils.GsonUtils;
import com.allNews.web.Requests;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;

public class ManagerUpdateNewApp {
    public static JsonArrayRequest getUpdateNewAppRequest(final Context context,
                                                          final Handler handler) {
        String url = context.getResources().getString(R.string.url_update_new_app);


        return Requests.getRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                List<UpdateNewApp> result = new ArrayList<UpdateNewApp>();
                Type listType = new TypeToken<List<UpdateNewApp>>() {
                }.getType();

                try {
                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);

                    saveUpdateNewAppInThread(context, result, handler);

                } catch (Exception e) {
                    handler.sendEmptyMessage(1);
                    Log.e("getNewAppRequestNewApi", e.toString());
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

    private static void saveUpdateNewAppInThread(final Context context, final List<UpdateNewApp> result, final Handler handler) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    deleteOldUpdatesApp(context, result, handler);
                    saveUpdateNewApp(context, result, handler);

                } catch (SQLException e) {

                    e.printStackTrace();
                } finally {
                    if (handler != null)
                        handler.sendEmptyMessage(1);

                }
            }

        }).start();
    }

    private static void deleteOldUpdatesApp(Context context, List<UpdateNewApp> result, final Handler handler ) throws SQLException {
        if (result != null && result.size() > 0) {
            Dao<UpdateNewApp, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getUpdateNewAppsDao();
            DeleteBuilder<UpdateNewApp, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.delete();

        }

    }

    private static void saveUpdateNewApp(Context context, List<UpdateNewApp> result, final Handler handler) throws SQLException {
        Dao<UpdateNewApp, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getUpdateNewAppsDao();
        int size = result != null ? result.size() : 0;
        for (int index = 0; index < size; index++) {
            UpdateNewApp updateNewApp = result.get(index);
            dao.create(updateNewApp);

        }
        getNewAppIThread(context, handler);

    }

    private static void getNewAppIThread(final Context context, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getListOfNodeLink(context, handler);
                } catch (SQLException e) {
                    e.printStackTrace();
                }finally {
                    if (handler != null)
                        handler.sendEmptyMessage(1);
                }
            }
        }).start();


    }

    private static void getListOfNodeLink(Context context, final Handler handler) throws SQLException {
        Dao<UpdateNewApp, Integer> daoUpdate = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getUpdateNewAppsDao();
       List<UpdateNewApp> listUpdates = new ArrayList<>();
        listUpdates = daoUpdate.queryForAll();
        ManagerNewAppNewApi.getNewAppUrl(context, listUpdates, handler);
    }


}
