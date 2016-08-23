package com.allNews.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.allNews.application.App;
import com.allNews.data.Categories;
import com.allNews.data.CategoriesNewApp;
import com.allNews.data.Event;
import com.allNews.data.History;
import com.allNews.data.NewApp.NewApp;
import com.allNews.data.NewApp.TagNewApp;
import com.allNews.data.News;
import com.allNews.data.Source;
import com.allNews.data.TaxonomyNewApp;
import com.allNews.data.Update;
import com.allNews.data.UpdateNewApp;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;

import gregory.network.rss.BuildConfig;
import gregory.network.rss.R;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static String DB_PATH = Environment.getExternalStorageDirectory()
//            .getAbsolutePath()
            + "/" + BuildConfig.FLAVOR + "/";

    public static String DATABASE_NAME = "news.db";
    public static final int DATABASE_VERSION = 28;

    private Context context;

    private Dao<Categories, Integer> categoryDao = null;
    private Dao<Source, Integer> sourceDao = null;
    private Dao<History, Integer> historyDao = null;
    private Dao<News, Integer> newsDao = null;
    private Dao<Event, Integer> eventDao = null;
    private Dao<Update, Integer> updateDao = null;
    private Dao<CategoriesNewApp, Integer> categoriesNewAppDao = null;
    private Dao<NewApp, Integer> newAppDao = null;
    private Dao<UpdateNewApp, Integer> updateNewAppsDao = null;
    private Dao<TaxonomyNewApp, Integer> taxonomyNewAppsDao = null;
    private Dao<TagNewApp, Integer> tagNewAppsDao = null;

    public DatabaseHelper(Context context) {
        super(context, DB_PATH + DATABASE_NAME, null, DATABASE_VERSION);
        context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        initDataBase(db);
    }

    private void initDataBase(SQLiteDatabase theDb) {

        theDb.execSQL("PRAGMA foreign_keys=ON;");

        try {
            TableUtils.createTableIfNotExists(connectionSource, NewApp.class);
            TableUtils.createTableIfNotExists(connectionSource, UpdateNewApp.class);
            TableUtils.createTableIfNotExists(connectionSource, TaxonomyNewApp.class);
            TableUtils.createTableIfNotExists(connectionSource, TagNewApp.class);
            TableUtils.createTableIfNotExists(connectionSource, Categories.class);
            TableUtils.createTableIfNotExists(connectionSource, Source.class);
            TableUtils.createTableIfNotExists(connectionSource, History.class);
            TableUtils.createTableIfNotExists(connectionSource, News.class);
            TableUtils.createTableIfNotExists(connectionSource, Event.class);
            TableUtils.createTableIfNotExists(connectionSource, Update.class);
            TableUtils.createTableIfNotExists(connectionSource, CategoriesNewApp.class);

            Log.e(DatabaseHelper.class.getSimpleName(), "createDB");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseHelper.class.getSimpleName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        Log.e(DatabaseHelper.class.getSimpleName(), "OnUpgrade");
        //Log.e("oldVersion " + oldVersion, " newVersion " + newVersion);
        if (oldVersion == 15 && newVersion == 16) {
            try {
                TableUtils.createTableIfNotExists(connectionSource,
                        CategoriesNewApp.class);
                Log.e(DatabaseHelper.class.getSimpleName(), "Create table CategoryNewApp");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
        if (oldVersion < 21) {

            try {
                Dao<News, Integer> newses = getNewsDao();
                newses.executeRaw("ALTER TABLE `table_news` ADD COLUMN isB2B INTEGER;");
                Log.e("DB", "add B2B");
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
        if (oldVersion < 22) {
            Dao<Source, Integer> sources = null;

            try {
                sources = getSourceDao();
                UpdateBuilder<Source, Integer> updateBuilder = sources.updateBuilder();
                updateBuilder.updateColumnValue("isActive", 1);
                updateBuilder.where().eq("isActive", 0).and().eq("id", context.getResources().getString(R.string.ad_source_id));
                updateBuilder.update();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

        if (oldVersion < 24) {
            Dao<Source, Integer> sources = null;
            ArrayList<Integer> listID = new ArrayList<>();
            listID.add(104);
            listID.add(32);
            listID.add(19);
            listID.add(111);
            listID.add(92);
            listID.add(221);
            listID.add(144);
            listID.add(189);
            listID.add(89);
            listID.add(87);
            listID.add(191);
            listID.add(193);
            listID.add(23);
            listID.add(4);
            listID.add(26);
            listID.add(29);
            listID.add(6);
            listID.add(7);
            listID.add(94);
            listID.add(8);
            listID.add(199);
            listID.add(157);
            listID.add(225);
            try {
                sources = getSourceDao();
                UpdateBuilder<Source, Integer> updateBuilder = sources.updateBuilder();
                updateBuilder.updateColumnValue("isActive", 1);
                for (Integer integer : listID) {
                    updateBuilder.where().eq("isActive", 0).and().eq("id", integer);
                    updateBuilder.update();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (oldVersion < 25) {
            try {
                Dao<News, Integer> newses = getNewsDao();
                newses.executeRaw("ALTER TABLE `table_news` ADD COLUMN likesCount INTEGER;");
                newses.executeRaw("ALTER TABLE `table_news` ADD COLUMN dislikesCount INTEGER;");
                newses.executeRaw("ALTER TABLE `table_news` ADD COLUMN isLike INTEGER;");
                newses.executeRaw("ALTER TABLE `table_news` ADD COLUMN isDislike INTEGER;");
                Log.e("DB", "add likes");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dropDB(db);
            initDataBase(db);
        }
        if (oldVersion < 26) {
         /*   try {
                Dao<Categories, Integer> sources = getCategoryDao();
                UpdateBuilder<Categories, Integer> ub = sources.updateBuilder();
                ub.where().eq("isActive", 1);
                ub.updateColumnValue("isActive", 0);
                ub.update();
            } catch (SQLException e) {
                e.printStackTrace();
            }*/
        }
        if(oldVersion < 27) {

            try {
                Dao<News, Integer> newses = getNewsDao();
                newses.executeRaw("ALTER TABLE `table_news` ADD COLUMN isArticle INTEGER;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(oldVersion < 28) {
            try {
                Dao<News, Integer> newses = getNewsDao();
                newses.executeRaw("ALTER TABLE `table_events` ADD COLUMN country STRING;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    private void dropDB(SQLiteDatabase theDb) {
        Log.e(DatabaseHelper.class.getSimpleName(), "drop db");


        try {
            TableUtils.dropTable(connectionSource, Categories.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(connectionSource, Event.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // TableUtils.dropTable(connectionSource, Source.class, true);
        try {
            TableUtils.dropTable(connectionSource, History.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //TableUtils.dropTable(connectionSource, News.class, true);
        try {
            TableUtils.dropTable(connectionSource, Update.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils
                    .dropTable(connectionSource, CategoriesNewApp.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(connectionSource, NewApp.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(connectionSource, TaxonomyNewApp.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(connectionSource, UpdateNewApp.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(connectionSource, TagNewApp.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Dao<Categories, Integer> getCategoryDao() throws SQLException {
        if (categoryDao == null) {
            categoryDao = getDao(Categories.class);
        }
        return categoryDao;
    }

    public Dao<Source, Integer> getSourceDao() throws SQLException {
        if (sourceDao == null) {
            sourceDao = getDao(Source.class);
        }
        return sourceDao;
    }

    public Dao<History, Integer> getHistoryDao() throws SQLException {
        if (historyDao == null) {
            historyDao = getDao(History.class);
        }
        return historyDao;
    }

    public Dao<News, Integer> getNewsDao() throws SQLException {
        if (newsDao == null) {
            newsDao = getDao(News.class);
        }
        return newsDao;
    }

    public Dao<Event, Integer> getEventDao() throws SQLException {
        if (eventDao == null) {
            eventDao = getDao(Event.class);
        }
        return eventDao;
    }

    public Dao<Update, Integer> getUpdateDao() throws SQLException {
        if (updateDao == null) {
            updateDao = getDao(Update.class);
        }
        return updateDao;
    }

    public Dao<CategoriesNewApp, Integer> getCategoriesNewAppDao()
            throws SQLException {
        if (categoriesNewAppDao == null) {
            categoriesNewAppDao = getDao(CategoriesNewApp.class);
        }
        return categoriesNewAppDao;
    }

    public Dao<NewApp, Integer> getNewAppDao() throws SQLException {
        if (newAppDao == null) {
            newAppDao = getDao(NewApp.class);
        }
        return newAppDao;
    }

    public Dao<UpdateNewApp, Integer> getUpdateNewAppsDao() throws SQLException {
        if (updateNewAppsDao == null) {
            updateNewAppsDao = getDao(UpdateNewApp.class);
        }
        return updateNewAppsDao;
    }

    public Dao<TaxonomyNewApp, Integer> getTaxonomyNewAppsDao() throws SQLException {
        if (taxonomyNewAppsDao == null) {
            taxonomyNewAppsDao = getDao(TaxonomyNewApp.class);
        }
        return taxonomyNewAppsDao;
    }

    public Dao<TagNewApp, Integer> getTagNewAppsDao() throws SQLException {
        if (tagNewAppsDao == null) {
            tagNewAppsDao = getDao(TagNewApp.class);
        }
        return tagNewAppsDao;
    }

    @Override
    public void close() {
        super.close();
        categoryDao = null;
        sourceDao = null;
        historyDao = null;
        newsDao = null;
        eventDao = null;
        updateDao = null;
        categoriesNewAppDao = null;
        newAppDao = null;
        updateNewAppsDao = null;
        taxonomyNewAppsDao = null;
        tagNewAppsDao = null;

    }
}
