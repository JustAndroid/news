package com.allNews.managers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.allNews.activity.NewsFragment;
import com.allNews.activity.Preferences;
import com.allNews.activity.TabFragment;
import com.allNews.application.App;
import com.allNews.data.History;
import com.allNews.data.Likes;
import com.allNews.data.NewApp.NewApp;
import com.allNews.data.NewApp.TagNewApp;
import com.allNews.data.News;
import com.allNews.data.Source;
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
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gregory.network.rss.R;

public class ManagerNews {

    public static JsonArrayRequest getNewsRequest(final String urlNews,
                                                  final Context context, final Handler handler) {
        Log.e("getNewsRequest", urlNews);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        final int timeToSave = Integer.parseInt(sp.getString(
                Preferences.PREF_SAVE, "12"));

        return Requests.getRequest(urlNews, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                // response = Utils.clearResponseFromBOM(response);
                // Log.e("getNewsRequest response", "" + response);

                List<News> result = new ArrayList<>();
                Type listType = new TypeToken<List<News>>() {
                }.getType();
                try {
                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);
                    if (result.size() > 0) {
                        if (handler != null) {
                            Message msg = new Message();
                            msg.what = Loader.UPDATE_PROGRESS;
                            msg.arg1 = result.size();

                            handler.sendMessage(msg);
                        }
                        saveNewsInThread(context, urlNews, timeToSave, result,
                                handler);
                    } else {
                        {
                            ManagerUpdates.removeurl(context, urlNews);
                            if (handler != null)
                                handler.sendEmptyMessage(Loader.UPDATE_NEXT);
                        }
                    }
                } catch (Exception e) {
                    Log.e("getNewsRequest", "" + e.toString());
                    if (handler != null)
                        handler.sendEmptyMessage(Loader.UPDATE_STOP);
                }

            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //		Statistic.sendErrorStatistic(context, Statistic.CATEGORY_ERROR,
                //				Statistic.ACTION_ERROR_DOWNLOAD_NEWS, "" + error, 0L);
                //		Utils.isOnlineAfterError(context);
                Log.e("ManagerNews error", "" + error);
                if (handler != null)
                    handler.sendEmptyMessage(Loader.UPDATE_STOP);
            }
        });
    }

    public static JsonArrayRequest getLikeRequest(final Context context) {
        String url = context.getResources().getString(R.string.url_domain)
                + context.getResources()
                .getString(R.string.get_likes_url_);

        final JSONObject obj = new JSONObject();

        final JSONArray arrayNews = new JSONArray();
        List<Integer> sourceIds = null;
        try {
            sourceIds = getSourceIdList(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (sourceIds != null) {
            for (Integer sourceId : sourceIds) {
                arrayNews.put(sourceId);
            }
        }

        try {

            obj.put("data", arrayNews);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        url = url + "&data=" + obj.toString();
        Log.e("Likes url", url);
        return Requests.getRequest(url, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                // response = Utils.clearResponseFromBOM(response);
                // Log.e("getTopNewsRequest response", "" + response);

                List<Likes> result = new ArrayList<>();
                Type listType = new TypeToken<List<Likes>>() {
                }.getType();

                result = GsonUtils.getGson().fromJson(response.toString(),
                        listType);
                saveLikesInThread(context, result);

            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("LikeGet error", "" + error);

            }
        });
    }

    private static void saveLikesInThread(final Context context, final List<Likes> result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    saveLikes(context, result);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void saveLikes(Context context, List<Likes> result) throws SQLException {

        Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewsDao();
        UpdateBuilder<News, Integer> updateBuilder = dao.updateBuilder();
        for (Likes likes : result) {
            updateBuilder.updateColumnValue("likesCount", Integer.valueOf(likes.getLike()));
            updateBuilder.updateColumnValue("disLikesCount", Integer.valueOf(likes.getDislike()));
            updateBuilder.where().eq("id", Integer.valueOf(likes.getNewsID()));
            updateBuilder.update();
        }


    }

    public static JsonArrayRequest getTopNewsRequest(final Context context,
                                                     final Handler handler) {
        String urlNews = context.getResources().getString(R.string.url_base)
                + context.getResources().getString(R.string.top_news_url)
                + context.getResources().getString(R.string.app_id);
        Log.d("getTopNewsRequest", urlNews);

        return Requests.getRequest(urlNews, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                // response = Utils.clearResponseFromBOM(response);
                // Log.e("getTopNewsRequest response", "" + response);

                List<News> result = new ArrayList<>();
                Type listType = new TypeToken<List<News>>() {
                }.getType();
                try {
                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);
                    if (result.size() == 20) {
                        // if (handler != null)
                        // handler.sendEmptyMessage(Loader.UPDATE_PROGRESS);

                        saveTopNewsInThread(context, result, handler);
                    } else {
                        if (handler != null)
                            handler.sendEmptyMessage(Loader.UPDATE_NEXT);
                    }
                } catch (Exception e) {
                    if (handler != null)
                        handler.sendEmptyMessage(Loader.UPDATE_STOP);
                }

            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //	Statistic.sendErrorStatistic(context, Statistic.CATEGORY_ERROR,
                //			Statistic.ACTION_ERROR_DOWNLOAD_NEWS, "" + error, 0L);
                //	Utils.isOnlineAfterError(context);
                Log.e("ManagerNews error", "" + error);
                if (handler != null)
                    handler.sendEmptyMessage(Loader.UPDATE_STOP);
            }
        });
    }

    public static JsonArrayRequest getB2BNewsRequest(final Context context
    ) {
        long threeDays = System.currentTimeMillis() - 3 * ManagerUpdates.DAY;
        String urlNews = context.getResources().getString(R.string.url_base) +
                "&" + context.getResources().getString(R.string.urlB2BSource) +
                "&after=" + Utils.getDate(threeDays).replace(" ", "%20");

        Log.e("getB2BNewsRequest", urlNews);

        return Requests.getRequest(urlNews, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                // response = Utils.clearResponseFromBOM(response);
                // Log.e("getTopNewsRequest response", "" + response);

                List<News> result = new ArrayList<>();
                Type listType = new TypeToken<List<News>>() {
                }.getType();
                try {
                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);
                    if (result.size() > 0) {
                        // if (handler != null)
                        // handler.sendEmptyMessage(Loader.UPDATE_PROGRESS);

                        saveB2BInThread(context, result);
                    }
                } catch (Exception ignored) {

                }

            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //	Statistic.sendErrorStatistic(context, Statistic.CATEGORY_ERROR,
                //			Statistic.ACTION_ERROR_DOWNLOAD_NEWS, "" + error, 0L);
                //	Utils.isOnlineAfterError(context);
                Log.e("ManagerNews error", "" + error);

            }
        });
    }

    private static void saveB2BInThread(final Context context, final List<News> result) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    removeB2B(context);
                    saveNews(context, result, false, true, false);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static JsonArrayRequest getArticlesRequest(final Context context
    ) {
        long oneYear = System.currentTimeMillis() - 180 * ManagerUpdates.DAY;
        News news = getArticleLastDate(context);
        String urlNews;
        if (news != null) {
            urlNews = context.getResources().getString(R.string.url_base) +
                    "&" + "list=230" +
                    "&after=" + Utils.getDate(news.getUpdateTime()).replace(" ", "%20");
        } else {
            urlNews = context.getResources().getString(R.string.url_base) +
                    "&" + "list=230" +
                    "&after=" + Utils.getDate(oneYear).replace(" ", "%20");
        }

        return Requests.getRequest(urlNews, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                // response = Utils.clearResponseFromBOM(response);
                // Log.e("getTopNewsRequest response", "" + response);

                List<News> result = new ArrayList<>();
                Type listType = new TypeToken<List<News>>() {
                }.getType();
                try {
                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);
                    if (result.size() > 0) {
                        // if (handler != null)
                        // handler.sendEmptyMessage(Loader.UPDATE_PROGRESS);

                        saveArticlesInThread(context, result);
                    }
                } catch (Exception ignored) {

                }

            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //	Statistic.sendErrorStatistic(context, Statistic.CATEGORY_ERROR,
                //			Statistic.ACTION_ERROR_DOWNLOAD_NEWS, "" + error, 0L);
                //	Utils.isOnlineAfterError(context);
                Log.e("ManagerNews error", "" + error);

            }
        });
    }

    private static News getArticleLastDate(Context context) {

        try {
            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();

            QueryBuilder<News, Integer> queryBuilder = dao.queryBuilder().orderBy(
                    DBOpenerHelper.KEY_UPDATE_TIME, false);
            queryBuilder.where().eq("isArticle", 1);
            queryBuilder.limit(1L);
            return dao.queryForFirst(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveArticlesInThread(final Context context, final List<News> result) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    saveNews(context, result, false, false, true);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }).start();

    }

    private static void saveNews(Context context, List<News> newsList, boolean isTop, boolean isB2B, boolean isArticle) throws SQLException {
        Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewsDao();
        int size = newsList != null ? newsList.size() : 0;
        for (int index = 0; index < size; index++) {
            News news = newsList.get(index);
            long count = dao.queryBuilder().where()
                    .eq(DBOpenerHelper.KEY_ID, news.getNewsID()).countOf();
            if (count > 0) {
                if (isB2B) {
                    UpdateBuilder<News, Integer> updateBuilder = dao
                            .updateBuilder();
                    updateBuilder.updateColumnValue("isB2B", 1);

                    updateBuilder.where().eq("id", news.getNewsID());

                    updateBuilder.update();
                }
            } else {

                if (isB2B) {
                    setNewsInfo(context, news);
                    news.setIsB2B(1);

                    dao.createIfNotExists(news);
                }
                if (isArticle) {
                    setNewsInfo(context, news);
                    news.setIsArticle(1);

                    dao.createIfNotExists(news);
                }
            }
        }

    }

    private static void removeB2B(Context context) throws SQLException {
        long threeDaysAgo = System.currentTimeMillis() - ManagerUpdates.DAY * 3;
        Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewsDao();
        UpdateBuilder<News, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.updateColumnValue("isB2B", 0);
        updateBuilder.where().eq("isB2B", 1).and().lt("updateTime", threeDaysAgo);
        updateBuilder.update();
    }


    protected static void saveTopNewsInThread(final Context context,

                                              final List<News> result,

                                              final Handler handler) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    removeOldTop(context);
                    saveNews(context, result, true);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (handler != null)
                        handler.sendEmptyMessage(Loader.UPDATE_NEXT);

                }
            }
        }).start();

    }

    protected static void removeOldTop(Context context) throws SQLException {
        Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewsDao();
        UpdateBuilder<News, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.updateColumnValue("isTop", 0);
        updateBuilder.where().eq("isTop", 1).and().eq("isNewApp", 0);

        updateBuilder.update();

    }

    protected static void saveNewsInThread(final Context context,
                                           final String urlNews, final int timeToSave,
                                           final List<News> result,

                                           final Handler handler) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    saveNews(context, result, false);
                    if (handler != null)
                        ManagerUpdates.removeurl(context, urlNews);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (handler != null)
                        handler.sendEmptyMessage(Loader.UPDATE_NEXT);
                }
            }
        }).start();

    }

    private static void saveNews(final Context context, List<News> newsList,
                                 boolean isTop) throws Exception {
        Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewsDao();
        int size = newsList != null ? newsList.size() : 0;
        for (int index = 0; index < size; index++) {
            News news = newsList.get(index);
            long count = dao.queryBuilder().where()
                    .eq(DBOpenerHelper.KEY_ID, news.getNewsID()).countOf();
            if (count > 0) {
                if (isTop) {
                    UpdateBuilder<News, Integer> updateBuilder = dao
                            .updateBuilder();
                    updateBuilder.updateColumnValue("isTop", 1);
                    updateBuilder.updateColumnValue("quantity",
                            news.getQuantity());

                    updateBuilder.where().eq("id", news.getNewsID());

                    updateBuilder.update();
                }
            } else {
                setNewsInfo(context, news);
                if (isTop)
                    news.setIsTop(1);

                dao.create(news);

            }
        }


    }

    private static void setNewsInfo(Context context, News news) {

        try {
            setSourceName(context, news);
            setCategories(context, news);
            long updateTime = news.getUpdateTime();
            if (updateTime == 0) {
                updateTime = Utils.getTime(news.getUpdateDate());
                news.setUpdateTime(updateTime);
            }

            long pubTime = news.getPubTime();
            if (pubTime == 0) {
                pubTime = Utils.getTime(news.getPubDate());
                news.setPubTime(pubTime);
            }

            if (!news.getImageUrl().equals("")
                    && !news.getImageUrl().startsWith("http://"))
                news.setImageUrl("http://" + news.getImageUrl());


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    public static void setCategories(Context context, News news) {

        List<String> categoryList = news.getCategory();

        try {
            String catId1 = categoryList.get(0);
            news.setCategory1(Integer.parseInt(catId1));

        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            news.setCategory1(-1);
        }

        try {
            String catId2 = categoryList.get(1);
            news.setCategory2(Integer.parseInt(catId2));

        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            news.setCategory2(-1);
        }

        try {
            String catId3 = categoryList.get(2);
            news.setCategory3(Integer.parseInt(catId3));

        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            news.setCategory3(-1);
        }

    }

    private static void setSourceName(Context context, News news)
            throws SQLException {
        Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getSourceDao();
        Source source = dao.queryBuilder().where()
                .eq(DBOpenerHelper.KEY_SOURCE_ID, news.getsourceID())
                .queryForFirst();
        if (source != null)
            news.setSource(source.getName());

    }

    public static void setNewsToHistory(final Context context, final int newsId, boolean isLike, boolean isDislike) {

        Dao<History, Integer> dao;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getHistoryDao();
            DeleteBuilder<History, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where()
                    .eq(DBOpenerHelper.KEY_HISTORY_NEWS_ID, newsId);
            deleteBuilder.delete();

            History hNews = new History();
            hNews.setNewsID(newsId);

            String time = "";
            try {// 2014-06-17%2013:20:00
                time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date(System.currentTimeMillis()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            hNews.setTime(time);
            if (isLike) {
                hNews.setIsLike(1);
            }
            if (isDislike) {
                hNews.setIsDisLike(1);
            }
            dao.create(hNews);
        } catch (java.sql.SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static List<News> getAllNews(final Context context, boolean onlyImportant, boolean onlyRead, boolean onlySelectedMedia, boolean onlyUnRead
            , boolean onlyNovN, long screenCount) {
        List<News> newses = null;

        try {

            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();
            QueryBuilder<News, Integer> queryBuilder = dao.queryBuilder()
                    .orderBy(DBOpenerHelper.KEY_PUB_TIME, false)
                    .limit(DBOpenerHelper.MAX_NEWS_IN_PAGE * screenCount);

            List<Integer> sourcesId = getSourceIdList(App.getContext());


            Where<News, Integer> where = null;

            if (onlyImportant)
                where = queryBuilder.where().eq("isMarked", "1");
            else if (onlyRead) {
                where = queryBuilder.where().eq("isRead", "1");
            } else if (onlySelectedMedia) {
                where = queryBuilder.where().eq(DBOpenerHelper.KEY_NEWS_SOURCE_ID, NewsFragment.sourcesId);
            } else if (onlyUnRead) {
                where = queryBuilder.where().eq("isShown", "0");
            } else if (onlyNovN) {
                where = queryBuilder.where().eq(DBOpenerHelper.KEY_NEWS_SOURCE_ID, 230);
            } else {

                List<String> columns = new ArrayList<>();
                columns.add(DBOpenerHelper.KEY_ID);
                columns.add(DBOpenerHelper.KEY_TITLE);
                columns.add("imageSmallUrl");
                columns.add("isMarked");
                columns.add(DBOpenerHelper.KEY_PUB_DATE);
                columns.add(DBOpenerHelper.KEY_PUB_TIME);
                columns.add("sourceName");
                columns.add(DBOpenerHelper.KEY_NEWS_SOURCE_ID);
                columns.add("isRead");
                columns.add("isTop");
                columns.add("isShown");
                columns.add("isB2B");
                columns.add("link");

                where = queryBuilder.selectColumns(columns).where()
                        .in(DBOpenerHelper.KEY_NEWS_SOURCE_ID, sourcesId);
                for (Integer integer : sourcesId) {
                    if (integer != null && String.valueOf(integer)
                            .equals(App.getContext().getResources().getString(R.string.ad_source_id))) {

                        ArrayList<News> news = (ArrayList<News>) where.query();
                        ArrayList<News> b2b = (ArrayList<News>) getB2BNews(App.getContext(), screenCount);

                        int position = 4;
                        int counter = 0;
                        for (int i = 0; i < b2b.size(); i++) {
                            News b2bNews = b2b.get(i);
                            if (news.size() > (position + counter) && b2bNews.isShown() == 0 && news.get(position + counter).getIsB2() == 0
                                    && news.get(position + counter + 1).getIsB2() == 0) {
                                long time1 = news.get(position + counter).getPubTime();
                                long time2 = news.get(position + counter + 1).getPubTime();
                                long timeB2B = (time1 + time2) / 2;
                                String pubDateB2B = Utils.getDate(timeB2B);
                                b2bNews.setPubTime(timeB2B);
                                b2bNews.setPubDate(pubDateB2B);
                                dao.update(b2bNews);

                                counter = counter + 20;
                            }


                        }
                        queryBuilder = dao.queryBuilder()
                                .orderBy(DBOpenerHelper.KEY_PUB_TIME, false)
                                .limit(DBOpenerHelper.MAX_NEWS_IN_PAGE * screenCount);
                        where = queryBuilder.selectColumns(columns).where()
                                .in(DBOpenerHelper.KEY_NEWS_SOURCE_ID, sourcesId);
                    }


                }


            }
            newses = where.query();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return newses;
    }


    private static List<Integer> getSourceIdList(Context context)
            throws SQLException {

        Dao<Source, Integer> daoSources = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getSourceDao();

        List<Source> sources = daoSources.queryBuilder().where()
                .eq(DBOpenerHelper.KEY_SOURCE_IS_ACTIVE, 1).query();
        List<Integer> sourcesId = new ArrayList<>();
        for (Source source : sources) {
            sourcesId.add(source.getSourceID());
        }

        return sourcesId;
    }

    public static News getNewsByIdFromDb(Context context, long newsId) {
        try {
            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();
            return dao.queryBuilder().where().eq(DBOpenerHelper.KEY_ID, newsId)
                    .queryForFirst();

        } catch (SQLException e) {
            return null;

        }

    }

    public static News getLatestNews(Context context) throws SQLException {
        Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewsDao();

        QueryBuilder<News, Integer> queryBuilder = dao.queryBuilder().orderBy(
                DBOpenerHelper.KEY_UPDATE_TIME, false);
        queryBuilder.limit(1L);
        return dao.queryForFirst(queryBuilder.prepare());

    }

    public static long getLatestNewsCount(Context context, long lastDate) {

        try {
            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();
            QueryBuilder<News, Integer> queryBuilder = dao.queryBuilder();

            return queryBuilder.where()
                    .ge(DBOpenerHelper.KEY_PUB_TIME, lastDate).countOf();
        } catch (SQLException e) {
            return -1;
        }

    }

    public static long getNewsCount(Context context, String categoryId) {

        try {

            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();

            QueryBuilder<News, Integer> queryBuilder = dao.queryBuilder();
            List<Integer> sourcesId = getSourceIdList(context);

            Where<News, Integer> where = queryBuilder.where();
            if (categoryId.equals(TabFragment.TAB_ALL)) {
                where.in(DBOpenerHelper.KEY_NEWS_SOURCE_ID, sourcesId);
            } else {
                where.eq("category1", categoryId);
                where.eq("category2", categoryId);
                where.eq("category3", categoryId);
                where.or(3).and()
                        .in(DBOpenerHelper.KEY_NEWS_SOURCE_ID, sourcesId);
            }
            return where.countOf();

        } catch (SQLException e) {
            return 0;

        }

    }

    public static List<News> getAllNewsByCategory(Context context,
                                                  long screenCount, String categoryId) {

        try {

            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();

            QueryBuilder<News, Integer> queryBuilder = dao.queryBuilder()
                    .orderBy(DBOpenerHelper.KEY_PUB_TIME, false)
                    .limit(DBOpenerHelper.MAX_NEWS_IN_PAGE * screenCount);
            List<Integer> sourcesId = getSourceIdList(context);

            Where<News, Integer> where = queryBuilder.where();

            where.eq("category1", categoryId);
            where.eq("category2", categoryId);
            where.eq("category3", categoryId);
            where.or(3).and().in(DBOpenerHelper.KEY_NEWS_SOURCE_ID, sourcesId);
            // if (lastDate > 0)
            // where.and().le(DBOpenerHelper.KEY_PUB_TIME, lastDate);
            return where.query();

        } catch (SQLException e) {
            return new ArrayList<>();

        }

    }

    public static void tryClearDb(final Context context) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                EWLoader.clearCache2();
                try {
                    Dao<News, Integer> dao = OpenHelperManager.getHelper(
                            context, DatabaseHelper.class).getNewsDao();
                    DeleteBuilder<News, Integer> deleteBuilder = dao
                            .deleteBuilder();
                    deleteBuilder.where().eq("isMarked", 0);
                    deleteBuilder.delete();
                    Dao<NewApp, Integer> daoNewApp = OpenHelperManager.getHelper(context, DatabaseHelper.class).getNewAppDao();
                    DeleteBuilder<NewApp, Integer> deleteBuilderNewApp = daoNewApp
                            .deleteBuilder();
                    deleteBuilderNewApp.delete();
                    Dao<TagNewApp, Integer> daoTags = OpenHelperManager.getHelper(context, DatabaseHelper.class).getTagNewAppsDao();
                    DeleteBuilder<TagNewApp, Integer> deleteBuilderTags = daoTags
                            .deleteBuilder();
                    deleteBuilderTags.delete();
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.db_delete_success),
                                    Toast.LENGTH_LONG).show();

                        }
                    });
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void deleteOldNews(final Context context) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(context);

                long timeToSavePref = Integer.parseInt(sp.getString(
                        Preferences.PREF_SAVE, "12")) * 60 * 60 * 1000;

                long timeToDelNews = System.currentTimeMillis()
                        - timeToSavePref;

                try {
                    Dao<News, Integer> dao = OpenHelperManager.getHelper(
                            context, DatabaseHelper.class).getNewsDao();
                    DeleteBuilder<News, Integer> deleteBuilder = dao
                            .deleteBuilder();
                    deleteBuilder.where().eq("isMarked", "0").and()
                            .eq("isTop", "0").and().eq("isB2B", 0).and().eq("isArticle", 0).and()
                            .le(DBOpenerHelper.KEY_UPDATE_TIME, timeToDelNews);
                    deleteBuilder.delete();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static void updateNews(final Context context, String column,
                                  int newsId) {

        try {
            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();
            UpdateBuilder<News, Integer> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue(column, 1);
            updateBuilder.where().eq("id", newsId);

            updateBuilder.update();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void updateNewsSetShown(final Context context,
                                          ArrayList<Integer> newsId) {

        try {
            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();
            UpdateBuilder<News, Integer> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue("isShown", 1);
            updateBuilder.where().in("id", newsId);
            updateBuilder.update();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void deleteLastNews(final Context context) {

        long timeToDelNews = System.currentTimeMillis()
                - ManagerUpdates.HALF_HOUR * 24;// 20
        // *
        // 60
        // *
        // 1000;

        try {
            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();
            DeleteBuilder<News, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().ge(DBOpenerHelper.KEY_UPDATE_TIME,
                    timeToDelNews);
            deleteBuilder.delete();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static List<History> getNewsFromHistoryToSync(final Context context) {
        List<History> history = null;

        try {
            Dao<History, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getHistoryDao();

            QueryBuilder<History, Integer> queryBuilder = dao.queryBuilder()
                    .limit(10l);
            queryBuilder.where().eq("isLike", 0).and().eq("isDisLike", 0);
            history = queryBuilder.query();

        } catch (Exception e) {
            return null;
        }
        return history;

    }

    public static List<History> getLikeFromHistoryToSync(final Context context) {
        List<History> history = null;

        try {
            Dao<History, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getHistoryDao();

            QueryBuilder<History, Integer> queryBuilder = dao.queryBuilder()
                    .limit(10l);
            queryBuilder.where().eq("isLike", 1);
            history = queryBuilder.query();

        } catch (Exception e) {
            return null;
        }
        return history;

    }

    public static List<History> getDisLikeFromHistoryToSync(final Context context) {
        List<History> history = null;

        try {
            Dao<History, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getHistoryDao();

            QueryBuilder<History, Integer> queryBuilder = dao.queryBuilder()
                    .limit(10l);
            queryBuilder.where().eq("isDisLike", 1);
            history = queryBuilder.query();

        } catch (Exception e) {
            return null;
        }
        return history;

    }

    public static void deleteNewsFromHistory(final Context context) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    Dao<History, Integer> dao = OpenHelperManager.getHelper(
                            context, DatabaseHelper.class).getHistoryDao();

                    DeleteBuilder<History, Integer> deleteBuilder = dao
                            .deleteBuilder();

                    deleteBuilder.delete();

                } catch (java.sql.SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public static List<News> getNewsByCategory(Context context, String str,
                                               String categoryId) {

        try {
            // Long categoryId = ManagerCategories.getCategoryId(context,
            // catName);

            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();
            QueryBuilder<News, Integer> qb = dao.queryBuilder().orderBy(
                    DBOpenerHelper.KEY_PUB_TIME, false);

            Where<News, Integer> where = qb.where();

            where.eq("category1", categoryId);
            where.eq("category2", categoryId);
            where.eq("category3", categoryId);
            where.or(3).and().like("content", "%" + str + "%");

            PreparedQuery<News> pq = qb.prepare();
            return dao.query(pq);
        } catch (SQLException e) {
            return null;
        }

    }

    public static List<News> getNews(Context context, String str,
                                     boolean isFav, boolean isTop, boolean isRead, boolean isSelectedMedia, boolean isUnread, boolean isArticle) {
        Dao<News, Integer> dao;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getNewsDao();
            QueryBuilder<News, Integer> qb = dao.queryBuilder().orderBy(
                    DBOpenerHelper.KEY_PUB_TIME, false);

            Where<News, Integer> where = qb.where().like("content",
                    "%" + str + "%");

            if (isFav)
                where.and().eq("isMarked", "1");
            else if (isTop)
                where.and().eq("isTop", "1").and().eq("isNewApp", 0);
            else if (isRead)
                where.and().eq("isRead", "1").and().eq("isNewApp", 0);
            else if (isSelectedMedia)
                where.and().eq("sourceID", NewsFragment.sourcesId);
            else if (isUnread)
                where.and().eq("isShown", 0);
            else if (isArticle)
                where.and().eq("isArticle", 1);

            PreparedQuery<News> pq = qb.prepare();
            return dao.query(pq);
        } catch (SQLException e) {
            return null;
        }

    }

    public static List<News> getTopNewsForNotification(Context context) {
        Dao<News, Integer> dao;
        List<News> newses = null;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class).getNewsDao();
            QueryBuilder<News, Integer> qb = dao.queryBuilder().orderBy(
                    "pubDate", false);
            qb.limit(5l);
            qb.where().eq("isTop", 1);
            newses = qb.query();

        } catch (SQLException e) {
            e.printStackTrace();

        }
        if ((newses != null ? newses.size() : 0) == 5) {
            return newses;
        } else {
            return new ArrayList<>();
        }

    }

    public static List<News> getTopNews(Context context) {
        Dao<News, Integer> dao;
        Source source = null;
        News b2b = getB2BNewsForTop(context);
        List<News> news = null;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getNewsDao();
            QueryBuilder<News, Integer> qb = dao.queryBuilder().orderBy(
                    "quantity", false);

            qb.where().eq("isTop", 1).and().eq("isNewApp", 0);
            PreparedQuery<News> pq = qb.prepare();
            news = dao.query(pq);
            source = ManagerSources.getSourceById(context, Integer.valueOf(context.getResources().getString(R.string.ad_source_id)).intValue());
            if (source != null && source.getIsActive() == 1 && b2b != null && news != null && news.size() == 20) {

                news.add(news.size() / 2, b2b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return news;
    }


    public static List<News> getB2BNews(Context context, long screenCount) {
        Dao<News, Integer> dao;
        List<News> newses = null;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getNewsDao();
            QueryBuilder<News, Integer> qb = dao.queryBuilder().orderBy(DBOpenerHelper.KEY_PUB_TIME, false)
                    .limit(DBOpenerHelper.MAX_NEWS_IN_PAGE * screenCount);

            qb.where().eq("isB2B", 1).and().eq("isShown", 0);
            PreparedQuery<News> pq = qb.prepare();
            newses = dao.query(pq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newses;
    }

    public static News getB2BNewsForTop(Context context) {
        Dao<News, Integer> dao;
        News b2b = null;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getNewsDao();
            QueryBuilder<News, Integer> qb = dao.queryBuilder().orderBy(DBOpenerHelper.KEY_PUB_TIME, false);
            qb.where().eq("isB2B", 1).and().eq("isShown", 0);
            PreparedQuery<News> pq = qb.prepare();
            b2b = dao.queryForFirst(pq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return b2b;
    }

    public static void setShownB2B(Context context, int id) {
        try {
            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();
            UpdateBuilder<News, Integer> ub = dao.updateBuilder();
            ub.updateColumnValue("isShown", 1).where().eq("id", id);
            ub.update();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void setNewsLike(Context context, int id, boolean isLike) {
        try {
            Dao<News, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getNewsDao();
            UpdateBuilder<News, Integer> ub = dao.updateBuilder();
            if (isLike) {
                ub.updateColumnValue("isLike", 1).where().eq("id", id);
            } else {
                ub.updateColumnValue("isDislike", 1).where().eq("id", id);
            }
            ub.update();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // public static ArrayList<Integer> getNewsIdsByCategory(Context context,
    // String newsCategory) {
    // Dao<News, Integer> dao;
    // try {
    // dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
    // .getNewsDao();
    // QueryBuilder<News, Integer> qb = dao.queryBuilder()
    // .orderBy(DBOpenerHelper.KEY_PUB_TIME, false)
    // .selectColumns("id");
    //
    // Where<News, Integer> where = qb.where();
    // if (newsCategory.equals(TabFragment.TAB_TOP))
    // where.eq("isTop", 1).and().eq("isNewApp", 0);
    // else if (newsCategory.equals(TabFragment.TAB_FAV))
    // where.eq("isMarked", 1);
    // else if (newsCategory.equals(TabFragment.NEW_APP))
    // where.eq("isNewApp", 1);
    // else if (newsCategory.equals(TabFragment.TAB_ALL)) {
    // where.eq("isNewApp", 0);
    // } else {
    // where.eq("category1", newsCategory);
    // where.eq("category2", newsCategory);
    // where.eq("category3", newsCategory);
    // where.or(3);
    // }
    // PreparedQuery<News> pq = qb.prepare();
    // List<News> newsList = dao.query(pq);
    // return new ArrayList<Integer>();
    // } catch (SQLException e) {
    // return new ArrayList<Integer>();
    // }
    // }

}
