package com.allNews.managers;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.allNews.activity.Preferences;
import com.allNews.application.App;
import com.allNews.data.NewApp.NewApp;
import com.allNews.data.NewApp.TagNewApp;
import com.allNews.data.NewApp.Und;
import com.allNews.data.TaxonomyNewApp;
import com.allNews.data.UpdateNewApp;
import com.allNews.db.DatabaseHelper;
import com.allNews.utils.GsonUtils;
import com.allNews.web.Requests;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ManagerNewAppNewApi {
   private static RequestQueue requestQueue;
    protected static void getNewAppUrl(final Context context, List<UpdateNewApp> list, final Handler handler) {
       requestQueue = App.getRequestQueue();
        for (int index = 0; index < list.size(); index++) {
            int nodeID = list.get(index).getNodeID();
            String url = list.get(index).getNodeLink();
            try {
                Dao<NewApp, Integer> dao = OpenHelperManager.getHelper(context,
                        DatabaseHelper.class).getNewAppDao();
                QueryBuilder<NewApp, Integer> qb = dao.queryBuilder();
                NewApp newApp = qb.where().eq("nodeID", nodeID).queryForFirst();
                if (newApp == null) {
                    requestQueue.add(getNewAppRequest(context, url, handler));

                } else if (list.get(index).getNode_changed() != newApp.getNode_changed()) {
                    requestQueue.add(getNewAppRequest(context, url, handler));
                }
            } catch (SQLException e) {
                e.printStackTrace();

            }

        }
    }

    private static JsonRequest<JSONObject> getNewAppRequest(final Context context, String url, final Handler handler) {
        //  Log.e("getNewAppRequestNewApi", url);

        return Requests.getOjectRequest(url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                NewApp result;
                Type listType = new TypeToken<NewApp>() {
                }.getType();

                try {
                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);


                    saveNewAppInThread(context, result, handler);

                } catch (Exception e) {


                }

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("getNewAppRequestNewApi", "" + error);


            }
        });
    }


    protected static void saveNewAppInThread(final Context context, final NewApp result, final Handler handler) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                  //  ManagerTagsNewApp.removeOldTagsNewApp(context);
                    removeOrUpdateOldNewApp(context);
                    saveNewApp(context, result);

                } catch (Exception e) {

                    e.printStackTrace();
                } finally {
                    if (handler != null)
                        handler.sendEmptyMessage(1);

                }
            }

        }).start();

    }

    private static void saveNewApp(Context context, NewApp result) throws Exception {
        Dao<NewApp, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewAppDao();
        Dao<TagNewApp, Integer> tagNewAppsDao = OpenHelperManager.getHelper(context, DatabaseHelper.class).getTagNewAppsDao();
        Dao<TaxonomyNewApp, Integer> taxonomyNewApps = OpenHelperManager.getHelper(context, DatabaseHelper.class).getTaxonomyNewAppsDao();


        String content = result.getBody().getUnd().get(0).getContent();
        String summary = result.getBody().getUnd().get(0).getSummary();
        result.setSummary(summary);
        result.setContent(content);
        String imgUrl = result.getFieldImage().getUnd().get(0).getImgURL();
        result.setImgUrl(imgUrl);
        String refLink = result.getFieldLink().getUnd().get(0).getRefUrl();
       result.setRefLink(refLink);
        List<Und> unds =  result.getFieldTags().getUnd();

        ArrayList<TagNewApp> tags = new ArrayList<>();
        TagNewApp tagNewApp = new TagNewApp();
        TaxonomyNewApp taxonomyNewApp;
        for(Und und : unds){
            String tagID = und.getTagID();
            tagNewApp.setTagID(tagID);
            tagNewApp.setNewApp(result);
            taxonomyNewApp = taxonomyNewApps.queryBuilder().where().idEq(Integer.valueOf(tagID)).queryForFirst();
            tagNewApp.setTagName(taxonomyNewApp.getName());
            tags.add(tagNewApp);
            tagNewAppsDao.create(tagNewApp);
        }
        result.setTagNewApps(tags);
        dao.createOrUpdate(result);
    }


    public static void removeOrUpdateOldNewApp(Context context) throws SQLException {

        Dao<NewApp, Integer> daoNewApp = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewAppDao();
        QueryBuilder<NewApp, Integer> qbNewApp;
        DeleteBuilder<NewApp, Integer> deleteBuilderNewApp;
        Dao<TagNewApp, Integer> daoTagNewApps = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getTagNewAppsDao();
        QueryBuilder<TagNewApp, Integer> qbTagNewApp;
        DeleteBuilder<TagNewApp, Integer> deleteBuilderTags;
        Dao<UpdateNewApp, Integer> daoUpdatesNewApp = OpenHelperManager.getHelper(context, DatabaseHelper.class).getUpdateNewAppsDao();
        QueryBuilder<UpdateNewApp, Integer> qbUpdates = daoUpdatesNewApp.queryBuilder();

        PreparedQuery<UpdateNewApp> preparedQueryUpdate = qbUpdates.prepare();
        List<UpdateNewApp> updates = daoUpdatesNewApp.query(preparedQueryUpdate);
        ArrayList<UpdateNewApp> updateList = new ArrayList<>(updates);

        Iterator<UpdateNewApp> updateIterator = updateList.iterator();
        UpdateNewApp updateNewApp;
        long changedTime;
        int nodeID;
        while (updateIterator.hasNext()) {
            updateNewApp = updateIterator.next();

            nodeID = updateNewApp.getNodeID();

            changedTime = updateNewApp.getNode_changed();

            qbNewApp = daoNewApp.queryBuilder();
            NewApp newApp = qbNewApp.where().eq("nodeID", nodeID).queryForFirst();

            if (newApp != null) {

               // Log.e("getNewAppRequestNewApi","change time =  " + (newApp.getNode_changed() == changedTime) );
                if (newApp.getNode_changed() != changedTime) {
                    deleteBuilderNewApp = daoNewApp.deleteBuilder();
                    deleteBuilderNewApp.where().eq("nodeID", nodeID);
                    daoNewApp.delete(deleteBuilderNewApp.prepare());
                    deleteBuilderTags = daoTagNewApps.deleteBuilder();
                    deleteBuilderTags.where().eq("newApp_id", nodeID);
                    daoTagNewApps.delete(deleteBuilderTags.prepare());


                }
            }
        }


        qbNewApp = daoNewApp.queryBuilder();
        PreparedQuery<NewApp> preparedQueryNewApp = qbNewApp.prepare();
        List<NewApp> newAppList = daoNewApp.query(preparedQueryNewApp);
        List<NewApp> listNewApps = new ArrayList<>(newAppList);

        for (NewApp app : listNewApps) {
            nodeID = app.getNodeID();
            qbUpdates = daoUpdatesNewApp.queryBuilder();
            updateNewApp = qbUpdates.where().eq("nodeID", nodeID).queryForFirst();
            if (updateNewApp == null) {
                deleteBuilderNewApp = daoNewApp.deleteBuilder();
                deleteBuilderNewApp.where().eq("nodeID", nodeID);
                daoNewApp.delete(deleteBuilderNewApp.prepare());
                deleteBuilderTags = daoTagNewApps.deleteBuilder();
                deleteBuilderTags.where().eq("newApp_id", nodeID);
                daoTagNewApps.delete(deleteBuilderTags.prepare());

            }
        }


    }


    public static List<NewApp> getNewApp(Context context) {
        Dao<NewApp, Integer> dao;
        Dao<TagNewApp, Integer> daoTags;
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getNewAppDao();
            daoTags = OpenHelperManager.getHelper(context, DatabaseHelper.class).getTagNewAppsDao();
            QueryBuilder<TagNewApp, Integer> qbTags = daoTags.queryBuilder();
            QueryBuilder<NewApp, Integer> qb = dao.queryBuilder();


            PreparedQuery<NewApp> pq = qb.prepare();
            List<NewApp> listResult = dao.query(pq);

            List<NewApp> sortedListResult = new ArrayList<>();
            List<TagNewApp> tags;
            for (NewApp newApp : listResult) {
                tags  = qbTags.where().eq("newApp_id", newApp.getNodeID()).query();
                if(tags!= null && tags.size()>0)
                for (TagNewApp tagNewApp : tags){
                    if (tagNewApp.getTagName().equals(sp.getString(Preferences.COUNTRY_CODE, "UA")) || tagNewApp.getTagName().equals("AllCountry")){
                        sortedListResult.add(newApp);
                     break;
                    }
                }

            }

            return sortedListResult;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
    public static NewApp getNewAppByIdFromDb(Context context, long newsId) {
        try {
            Dao<NewApp, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewAppDao();
            return dao.queryBuilder().where().eq("nodeID", newsId)
                    .queryForFirst();

        } catch (SQLException e) {
            return null;

        }

    }
    public static ArrayList<String> getAllTabTag(Context context) {
        List<String> catList = ManagerTagsNewApp.getAllCategories(context);

        return new ArrayList<>(catList);
    }
    public static List<NewApp> getNewApp(Context context, String categoryId) {
        Dao<NewApp, Integer> dao;
        Dao<TagNewApp, Integer> tags;
        try {

            tags = OpenHelperManager.getHelper(context, DatabaseHelper.class).getTagNewAppsDao();
            QueryBuilder<TagNewApp, Integer> qb1 = tags.queryBuilder();

            Where<TagNewApp, Integer> whereTags = qb1.where();
            List<TagNewApp> tagNewApps = whereTags.eq("tagName", categoryId).query();

            List<NewApp> listResult = new ArrayList<>();
            for(TagNewApp tagNewApp : tagNewApps){
                listResult.add(tagNewApp.getNewApp());
            }

            // топ 5 бесплатных прилож            ТОП10 ПРИЛОЖ

            //  listResult.get(5).setCategory1(ManagerCategoriesNewApp.CATEGORY_BEST);
            //  listResult.get(7).setCategory1(ManagerCategoriesNewApp.CATEGORY_BEST);




            return listResult;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
    public static NewApp getNewAppForTOP20Footer(Context context) {

        List<NewApp> allNewApp = getNewAppForTop20(context);

        int newAppId = MyPreferenceManager.getNewAppId(context);
        if (newAppId == 0 && allNewApp.size() > 0) {
            MyPreferenceManager.setNewAppId(context, allNewApp.get(0)
                    .getNodeID());
            return allNewApp.get(0);
        }

        for (int i = 0; i < allNewApp.size(); i++) {
            if (newAppId == allNewApp.get(i).getNodeID()) {
                NewApp nextNewApp = null;
                if (allNewApp.size() - 1 == i) {
                    nextNewApp = allNewApp.get(0);
                } else {
                    nextNewApp = allNewApp.get(i + 1);
                }
                MyPreferenceManager
                        .setNewAppId(context, nextNewApp.getNodeID());
                return nextNewApp;
            }
        }
        if (allNewApp.size() > 0) {
            MyPreferenceManager.setNewAppId(context, allNewApp.get(0)
                    .getNodeID());
            return allNewApp.get(0);
        }
        return null;

    }

    public static List<NewApp> getNewAppForTop20(Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        try {
            Dao<NewApp, Integer> dao;
            Dao<TagNewApp, Integer> daoTags;
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getNewAppDao();
            QueryBuilder<NewApp, Integer> qb = dao.queryBuilder();
            daoTags = OpenHelperManager.getHelper(context, DatabaseHelper.class).getTagNewAppsDao();
            QueryBuilder<TagNewApp, Integer> qbTags = daoTags.queryBuilder();
            PreparedQuery<NewApp> pq = qb.prepare();
            List<NewApp> listResult = dao.query(pq);
            List<NewApp> sortedListResult = new ArrayList<>();
            List<TagNewApp> tags;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            for (NewApp newApp : listResult) {
                tags  = qbTags.where().eq("newApp_id", newApp.getNodeID()).query();
                if(tags!= null && tags.size()>0)
                    for (TagNewApp tagNewApp : tags){
                        if (tagNewApp.getTagName().equals(sp.getString(Preferences.COUNTRY_CODE, "UA")) || tagNewApp.getTagName().equals("AllCountry")){
                            sortedListResult.add(newApp);
                            break;
                        }
                    }

            }
            return sortedListResult;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

}

