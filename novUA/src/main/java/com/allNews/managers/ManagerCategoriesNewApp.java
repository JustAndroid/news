package com.allNews.managers;

import android.content.Context;

import com.allNews.activity.NewAppListFragment;
import com.allNews.data.CategoriesNewApp;
import com.allNews.db.DatabaseHelper;
import com.allNews.utils.GsonUtils;
import com.allNews.web.Requests;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
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

public class ManagerCategoriesNewApp {
    public static final int CATEGORY_TOP = 70;
    public static final int CATEGORY_BEST = 74;

    public static JsonArrayRequest getCategoriesRequest(final Context context,
                                                        Listener<JSONArray> listener, ErrorListener errorListener) {

        String urlCat = context.getResources().getString(R.string.url_base)
                + context.getResources().getString(
                R.string.new_app_categories_url);
        return Requests.getRequest(urlCat, listener, errorListener);
    }

    public static void saveCategories(final Context context,
                                      final JSONArray response) {

        try {
            List<CategoriesNewApp> result = new ArrayList<>();
            Type listType = new TypeToken<List<CategoriesNewApp>>() {
            }.getType();

            result = GsonUtils.getGson()
                    .fromJson(response.toString(), listType);
            deleteCategories(context);
            saveCategories(context, result);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void saveCategories(Context context,
                                       List<CategoriesNewApp> categoriesList) throws SQLException {
        Dao<CategoriesNewApp, Integer> dao = OpenHelperManager.getHelper(
                context, DatabaseHelper.class).getCategoriesNewAppDao();
        int size = categoriesList != null ? categoriesList.size() : 0;
        for (int index = 0; index < size; index++) {
            CategoriesNewApp category = categoriesList.get(index);
            // 0 and 70 - don't use this category
            if (category.getId() != 0 && category.getId() != CATEGORY_TOP && category.getId() != CATEGORY_BEST)
                dao.create(category);

        }
    }

    private static void deleteCategories(Context context) throws SQLException {

        Dao<CategoriesNewApp, Integer> dao = OpenHelperManager.getHelper(
                context, DatabaseHelper.class).getCategoriesNewAppDao();

        DeleteBuilder<CategoriesNewApp, Integer> deleteBuilder = dao
                .deleteBuilder();

        deleteBuilder.delete();

    }

    public static ArrayList<String> getAllTabTag(Context context) {
        List<CategoriesNewApp> catList = getAllCategories(context);
        ArrayList<String> tabTag = new ArrayList<>();
        tabTag.add(NewAppListFragment.TAB_ALL);


        for (CategoriesNewApp category : catList) {
            tabTag.add("" + category.getId());
        }


        return tabTag;
    }

    public static List<CategoriesNewApp> getAllCategories(Context context) {
        Dao<CategoriesNewApp, Integer> dao;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getCategoriesNewAppDao();
            return dao.queryBuilder().query();

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
