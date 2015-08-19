package com.allNews.managers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.allNews.activity.NewAppListFragment;
import com.allNews.data.News;
import com.allNews.db.DBOpenerHelper;
import com.allNews.db.DatabaseHelper;
import com.allNews.utils.GsonUtils;
import com.allNews.utils.Utils;
import com.allNews.web.Requests;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;

public class ManagerNewApp {

    public static JsonArrayRequest getNewAppRequest(final Context context,
                                                    final Handler handler) {
        String url = context.getResources().getString(R.string.url_base)
                + context.getResources().getString(R.string.new_app_url);
        Log.d("getNewAppUrl", url);

        return Requests.getRequest(url, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                List<News> result = new ArrayList<>();
                Type listType = new TypeToken<List<News>>() {
                }.getType();
                try {
                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);

                    saveNewAppInThread(context, result, handler);

                } catch (Exception e) {
                    handler.sendEmptyMessage(1);
                }

            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("ManagerNews error", "" + error);
                if (handler != null)
                    handler.sendEmptyMessage(2);
            }
        });
    }

    public static JsonArrayRequest getUpdateNewAppRequest(
            final Context context, final Handler handler) {
        String url = context.getResources().getString(R.string.url_base)
                + context.getResources().getString(R.string.new_app_url);
        String listIds = getNewAppIds(context);
        if (listIds != null)
            url = url + "&present_id=" + listIds;

        //	url = url  +  ",54363";
        Log.d("getNewAppUrl", " " + "getUpdateNewAppRequest " + " " + url);

        return Requests.getRequest(url, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                List<News> result;
                Type listType = new TypeToken<List<News>>() {
                }.getType();
                try {
                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);
                    updateNewAppInThread(context, result, handler);
                } catch (Exception e) {
                    handler.sendEmptyMessage(1);
                }

            }


        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("ManagerNews error", " " + "NEW APP REQUEST " + " " + error);
                if (handler != null)
                    handler.sendEmptyMessage(2);
            }
        });
    }

    private static void updateNewAppInThread(final Context context,
                                             final List<News> result, final Handler handler) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    updateNewApp(context, result);


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (handler != null)
                        handler.sendEmptyMessage(1);

                }
            }

        }).start();

    }

    protected static void updateNewApp(Context context, List<News> result) {
        try {
            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();
            for (News news : result) {
                if (news.getcontent() == null) {
                    DeleteBuilder<News, Integer> deleteBuilder = dao.deleteBuilder();
                    deleteBuilder.where().eq("id", news.getNewsID());
                    deleteBuilder.delete();
                    //int r = dao.delete(news);
                    //	Log.e("delete", "delete   "+r);
                } else {
                    setNewsTime(news);
                    ManagerNews.setCategories(context, news);
                    if (news.getCategory1() == ManagerCategoriesNewApp.CATEGORY_TOP
                            || news.getCategory2() == ManagerCategoriesNewApp.CATEGORY_TOP
                            || news.getCategory3() == ManagerCategoriesNewApp.CATEGORY_TOP)
                        news.setIsTop(1);
                    if (news.getCategory1() == ManagerCategoriesNewApp.CATEGORY_BEST
                            || news.getCategory2() == ManagerCategoriesNewApp.CATEGORY_BEST
                            || news.getCategory3() == ManagerCategoriesNewApp.CATEGORY_BEST) {
                        MyPreferenceManager.setNewAppHasBest(context, true);
                    }

                    news.setIsNewApp(1);
                    dao.create(news);
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

	
		
	/*	int size = newsList != null ? newsList.size() : 0;
        for (int index = 0; index < size; index++) {
			News news = newsList.get(index);
			long count = dao.queryBuilder().where()
					.eq(DBOpenerHelper.KEY_ID, news.getNewsID()).countOf();
			if (count > 0) {

			} else {
				setNewsTime(news);
				ManagerNews.setCategories(context, news);
				if (news.getCategory1() == ManagerCategoriesNewApp.CATEGORY_TOP
						|| news.getCategory2() == ManagerCategoriesNewApp.CATEGORY_TOP
						|| news.getCategory3() == ManagerCategoriesNewApp.CATEGORY_TOP)
					news.setIsTop(1);
				news.setIsNewApp(1);
				dao.create(news);
			}
		}
		*/
    }

    protected static void saveNewAppInThread(final Context context,

                                             final List<News> result,

                                             final Handler handler) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    removeOldNewApp(context);
                    saveNewApp(context, result);

                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (handler != null)
                        handler.sendEmptyMessage(1);

                }
            }

        }).start();

    }

    protected static void saveNewApp(Context context, List<News> newsList)
            throws SQLException {
        Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewsDao();
        int size = newsList != null ? newsList.size() : 0;
        for (int index = 0; index < size; index++) {
            News news = newsList.get(index);
            long count = dao.queryBuilder().where()
                    .eq(DBOpenerHelper.KEY_ID, news.getNewsID()).countOf();
            if (count > 0) {

            } else {
                setNewsTime(news);
                ManagerNews.setCategories(context, news);
                if (news.getCategory1() == ManagerCategoriesNewApp.CATEGORY_TOP
                        || news.getCategory2() == ManagerCategoriesNewApp.CATEGORY_TOP
                        || news.getCategory3() == ManagerCategoriesNewApp.CATEGORY_TOP)
                    news.setIsTop(1);
                news.setIsNewApp(1);
                dao.create(news);
            }
        }

    }

    private static void setNewsTime(News news) {
        long pubTime = news.getPubTime();
        if (pubTime == 0 ) {
            pubTime = Utils.getTime(news.getPubDate());
            news.setPubTime(pubTime);
        }

    }

    private static void removeOldNewApp(Context context) throws SQLException {
        Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewsDao();
        DeleteBuilder<News, Integer> deleteBuilder = dao.deleteBuilder();

        deleteBuilder.where().eq("isNewApp", 1);

        deleteBuilder.delete();
    }

    public static List<News> getNewApp(Context context, String categoryId) {
        Dao<News, Integer> dao;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getNewsDao();
            QueryBuilder<News, Integer> qb = dao.queryBuilder().orderBy(
                    "isTop", false);

            Where<News, Integer> where = qb.where();
            if (categoryId.equals(NewAppListFragment.TAB_ALL))
                where.eq("isNewApp", 1);
            else {
                where.eq("category1", categoryId);
                where.eq("category2", categoryId);
                where.eq("category3", categoryId);
                where.or(3);

                where.and().eq("isNewApp", 1);
            }
            PreparedQuery<News> pq = qb.prepare();
            List<News> listResult = dao.query(pq);
            // топ 5 бесплатных прилож            ТОП10 ПРИЛОЖ

            //  listResult.get(5).setCategory1(ManagerCategoriesNewApp.CATEGORY_BEST);
            //  listResult.get(7).setCategory1(ManagerCategoriesNewApp.CATEGORY_BEST);

            List<News> sortedListResult = new ArrayList<>();
            for (News news : listResult) {
                if (news.getCategory1() == ManagerCategoriesNewApp.CATEGORY_BEST
                        || news.getCategory2() == ManagerCategoriesNewApp.CATEGORY_BEST
                        || news.getCategory3() == ManagerCategoriesNewApp.CATEGORY_BEST) {
                    sortedListResult.add(0, news);
                    //  listResult.remove(news);
                } else
                    sortedListResult.add(news);
            }

            return sortedListResult;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public static List<News> getNewAppForTop20(Context context) {
        Dao<News, Integer> dao;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getNewsDao();
            QueryBuilder<News, Integer> qb = dao.queryBuilder();


            Where<News, Integer> where = qb.where();
            int categoryId = ManagerCategoriesNewApp.CATEGORY_BEST;
            where.eq("isNewApp", 1);

            where.and().eq("isTop", 1);
            where.eq("category1", categoryId);
            where.eq("category2", categoryId);
            where.eq("category3", categoryId);
            where.or(4);

            PreparedQuery<News> pq = qb.prepare();
            return dao.query(pq);
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public static News getNewAppForTOP20Footer(Context context) {

        List<News> allNewApp = getNewAppForTop20(context);

        int newAppId = MyPreferenceManager.getNewAppId(context);
        if (newAppId == 0 && allNewApp.size() > 0) {
            MyPreferenceManager.setNewAppId(context, allNewApp.get(0)
                    .getNewsID());
            return allNewApp.get(0);
        }

        for (int i = 0; i < allNewApp.size(); i++) {
            if (newAppId == allNewApp.get(i).getNewsID()) {
                News nextNewApp = null;
                if (allNewApp.size() - 1 == i) {
                    nextNewApp = allNewApp.get(0);
                } else {
                    nextNewApp = allNewApp.get(i + 1);
                }
                MyPreferenceManager
                        .setNewAppId(context, nextNewApp.getNewsID());
                return nextNewApp;
            }
        }
        if (allNewApp.size() > 0) {
            MyPreferenceManager.setNewAppId(context, allNewApp.get(0)
                    .getNewsID());
            return allNewApp.get(0);
        }
        return null;

    }

    private static String getNewAppIds(Context context) {
        Dao<News, Integer> dao;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getNewsDao();
            QueryBuilder<News, Integer> qb = dao.queryBuilder().selectColumns(
                    "id");

            Where<News, Integer> where = qb.where().eq("isNewApp", 1);

            PreparedQuery<News> pq = qb.prepare();

            List<News> list = dao.query(pq);
            if (list.size() == 0)
                return null;
            String result = "";
            for (News news : list) {
                result = result + news.getNewsID() + ",";
            }
            return result.substring(0, result.length() - 1);
        } catch (Exception e) {
            return null;
        }
    }

    public static JsonArrayRequest getNewAppRequestTest(final Context context,
                                                        final Handler handler) {
        String url = context.getResources().getString(R.string.url_base)
                + "&id=359554,359555,359556,999999,888888&all";
        Log.d("getNewAppUrl", url);

        return Requests.getRequest(url, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                List<News> result = new ArrayList<>();
                Type listType = new TypeToken<List<News>>() {
                }.getType();
                try {
                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);

                    saveNewAppInThread(context, result, handler);

                } catch (Exception e) {
                    handler.sendEmptyMessage(1);
                }

            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("ManagerNews error", "" + error);
                if (handler != null)
                    handler.sendEmptyMessage(2);
            }
        });
    }

}
