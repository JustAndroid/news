package com.allNews.managers;


import android.content.Context;

import com.allNews.data.NewApp.TagNewApp;
import com.allNews.db.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManagerTagsNewApp {

    public static List<String> getAllCategories(Context context) {
        Dao<TagNewApp, Integer> dao;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getTagNewAppsDao();
            List<TagNewApp> tagNewApps = dao.queryBuilder().where().eq("TagID","18").or().eq("TagID","15").or()
                    .eq("TagID","19").or().eq("TagID","17").or().eq("TagID","20")
                    .or().eq("TagID","23").query();
            Set<String> catSet = new HashSet<>();
            for (TagNewApp tagNewApp: tagNewApps){
                catSet.add(tagNewApp.getTagName());
            }
            return new ArrayList<>(catSet);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    /*public static void removeOldTagsNewApp (Context context) throws SQLException {

            Dao<TagNewApp, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getTagNewAppsDao();
            DeleteBuilder<TagNewApp, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.delete();

        }*/
    }

