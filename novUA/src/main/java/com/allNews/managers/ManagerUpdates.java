package com.allNews.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.allNews.activity.Preferences;
import com.allNews.data.News;
import com.allNews.data.Source;
import com.allNews.data.Update;
import com.allNews.db.DatabaseHelper;
import com.allNews.utils.Utils;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;

public class ManagerUpdates {
    public static final long MINUTE = 60 * 1000;
    public static final long TEN_MINUTE = 10 * MINUTE;
    public static final long HALF_HOUR = 30 * MINUTE;
    public static final long DAY = 48 * HALF_HOUR;

    public static void addUpdateUrl(Context context,
                                    List<Update> timesForUpdateList) {
        try {
            Dao<Update, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getUpdateDao();

            for (int i = timesForUpdateList.size() - 1; i >= 0; i--) {
                Update update = timesForUpdateList.get(i);

                long count = dao.queryBuilder().where()
                        .le("startTime", update.getStartTime()).and()
                        .ge("endTime", update.getStartTime()).countOf();
                if (count > 0) {
                    dao.update(update);
                } else {

                    dao.create(update);

                }

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static Update getLastUpdateUrl(Context context) {
        try {
            Dao<Update, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getUpdateDao();
            return dao.queryBuilder().orderBy("id", false).limit(1L)
                    .queryForFirst();

        } catch (SQLException e) {
            return null;
        }
    }

    public static void removeurl(Context context, String url) {
        try {
            Dao<Update, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getUpdateDao();
            DeleteBuilder<Update, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("updateUrl", url);
            deleteBuilder.delete();

        } catch (SQLException ignored) {

        }

    }

    public static void removeOldUrl(Context context) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);

        try {
            long maxHoursToSaveNews = System.currentTimeMillis()
                    - Integer.parseInt(sp
                    .getString(Preferences.PREF_SAVE, "12")) * 60 * 60
                    * 1000;
            Dao<Update, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getUpdateDao();
            DeleteBuilder<Update, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().le("endTime", maxHoursToSaveNews);
            deleteBuilder.delete();

        } catch (Exception ignored) {

        }

    }

    public static String getNewsToDownloadPeriod(Update update) {

        String end = "";
        String begin = Utils.getTimeHHMM(update.getStartTime());
        if (update.getEndTime() == 0)
            end = Utils.getTimeHHMM(System.currentTimeMillis());
        else
            end = Utils.getTimeHHMM(update.getEndTime());

        return begin + "-" + end;

    }

    public static void removeAllUrl(Context context) {
        try {
            Dao<Update, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getUpdateDao();
            DeleteBuilder<Update, Integer> deleteBuilder = dao.deleteBuilder();

            deleteBuilder.delete();

        } catch (Exception ignored) {

        }

    }

    public static void getListOfUrl(Context context, String sourceList) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        int maxHoursToSaveNews = 12;
        try {
            maxHoursToSaveNews = Integer.parseInt(sp.getString(
                    Preferences.PREF_SAVE, "12"));

            News news = ManagerNews.getLatestNews(context);

            String latestNewsDate = null;
            long latestNewsTime = 0;
            if (news != null) {
                latestNewsDate = news.getUpdateDate();
                latestNewsTime = news.getUpdateTime();
            }
            String url = context.getResources().getString(R.string.url_base);

            long currentTime = System.currentTimeMillis();

            // db empty , select new source, last news very old
            if ((latestNewsDate == null)
                    || (sourceList != null && sourceList.length() > 0)
                    || ((currentTime - latestNewsTime) > (maxHoursToSaveNews / 12)
                    * DAY)) {
                sourceList = getSources(context, sourceList);

                getList(context, url, 0, 6 * maxHoursToSaveNews, sourceList);
                return;
            }

            sourceList = getSources(context, sourceList);

            // last update less then 10 min
            if (((currentTime - latestNewsTime) < TEN_MINUTE)) {
                getOneUrl(context, url, latestNewsDate, latestNewsTime,
                        sourceList);
                return;
            }

            getList(context, url, latestNewsTime, sourceList);
        } catch (Exception ignored) {
        }
    }

    private static void getList(Context context, String url,
                                long latestNewsTime, String sourceList) {

        long currentTime = System.currentTimeMillis();

        int count = 1;

        long timeForUpdate = currentTime - latestNewsTime;

        do {
            timeForUpdate = timeForUpdate - TEN_MINUTE;
            count++;
        } while (timeForUpdate > TEN_MINUTE);

        getList(context, url, latestNewsTime, count, sourceList);
    }

    private static void getList(Context context, String url,
                                long latestNewsTime, int count, String sourceList) {
        List<Update> timesForUpdateList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        long beginTime = 0;
        long endTime = currentTime;
        String end;
        String begin;
        for (int i = 0; i < count; i++) {

            end = "end=" + Utils.getDate(endTime);
            if (i == (count - 1) && latestNewsTime > 0)
                beginTime = Utils.roundingToTenMinutes(latestNewsTime);
            else
                beginTime = Utils.roundingToTenMinutes(endTime - TEN_MINUTE);

            begin = "begin=" + Utils.getDate(beginTime);

            String time = "&" + end + "&" + begin;

            timesForUpdateList.add(new Update(url + sourceList
                    + time.replace(" ", "%20"), beginTime, endTime));
            endTime = beginTime - 1000;
        }
        ManagerUpdates.addUpdateUrl(context, timesForUpdateList);
    }

    private static void getOneUrl(Context context, String url,
                                  String latestNewsDate, long latestNewsTime, String sourceList) {
        List<Update> timesForUpdateList = new ArrayList<>();
        String time = "&after=" + latestNewsDate.replace(" ", "%20");

        timesForUpdateList.add(new Update(url + sourceList
                + time.replace(" ", "%20"), latestNewsTime, System
                .currentTimeMillis()));
        ManagerUpdates.addUpdateUrl(context, timesForUpdateList);

    }

    private static String getSources(Context context, String sourceList)
            throws SQLException {
        if (sourceList == null || sourceList.length() == 0)
            return getSourceList(context);
        else
            return "&list=" + sourceList;
    }

    private static String getSourceList(Context context) throws SQLException {
        Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getSourceDao();

        String list = "";

        List<Source> sourceList = dao.queryForAll();
        for (int i = 0; i < sourceList.size(); i++) {
            if (sourceList.get(i).getIsActive() == 1)
                list = list + sourceList.get(i).getSourceID() + ",";
        }
        if (list.length() > 0) {
            list = list.substring(0, list.length() - 1);
            list = "&list=" + list;
        }

        return list;
    }

    public static String getUrlForQuantity(Context context) {
        try {
            Dao<Update, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getUpdateDao();

            QueryBuilder<Update, Integer> queryBuilder = dao.queryBuilder()
                    .orderBy("startTime", true).limit(1L);

            String start = Utils.getDate(
                    queryBuilder.queryForFirst().getStartTime()).replace(" ",
                    "%20");

            queryBuilder = dao.queryBuilder().orderBy("endTime", false)
                    .limit(1L);

            String end = Utils.getDate(
                    queryBuilder.queryForFirst().getEndTime()).replace(" ",
                    "%20");
            String list = "";
            String url = queryBuilder.queryForFirst().getUpdateUrl();
            int indexEnd = 0;
            if (url.contains("&end"))
                indexEnd = url.indexOf("&end");
            else if (url.contains("&after"))
                indexEnd = url.indexOf("&after");
            if (indexEnd > 0)
                list = url.substring(url.indexOf("&list="), indexEnd);
            else
                list = getSourceList(context);
            return context.getResources().getString(R.string.url_base) + list
                    + "&begin=" + start + "&end=" + end + "&only_quantyty";

        } catch (Exception e) {
            return null;
        }

    }

}
