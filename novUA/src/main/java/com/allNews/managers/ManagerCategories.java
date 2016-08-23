package com.allNews.managers;

import android.content.Context;

import com.allNews.data.Categories;
import com.allNews.data.News;
import com.allNews.db.DBOpenerHelper;
import com.allNews.db.DatabaseHelper;
import com.allNews.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagerCategories {

//	public static JsonArrayRequest getCategoriesRequest(final Context context) {
//
//		String urlCat = context.getResources().getString(R.string.url_base)
//				+ context.getResources().getString(R.string.url_get_categories);
//		return Requests.getRequest(urlCat, new Listener<JSONArray>() {
//
//			@Override
//			public void onResponse(JSONArray response) {
//				// Log.e("ManagerCategories response", "" + response);
//
//				saveInThread(context, response);
//
//			}
//		}, new ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				Log.e("ManagerCategories error", "" + error);
//
//			}
//		});
//	}

    public static void saveInThread(final Context context,
                                    final JSONArray response) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    List<Categories> result = new ArrayList<>();
                    Type listType = new TypeToken<List<Categories>>() {
                    }.getType();

                    result = GsonUtils.getGson().fromJson(response.toString(),
                            listType);
                    saveOrUpdateCategories(context, result);
                    deleteOldCategories(context, result);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void saveCategories(final Context context,
                                      final JSONArray response) {

        try {
            List<Categories> result = new ArrayList<>();
            Type listType = new TypeToken<List<Categories>>() {
            }.getType();

            result = GsonUtils.getGson().fromJson(response.toString(),
                    listType);
            saveOrUpdateCategories(context, result);
            deleteOldCategories(context, result);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private static void saveOrUpdateCategories(Context context,
                                               List<Categories> categoriesList) throws SQLException {
        Dao<Categories, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getCategoryDao();
        int size = categoriesList != null ? categoriesList.size() : 0;
        for (int index = 0; index < size; index++) {
            Categories category = categoriesList.get(index);
            long count = dao
                    .queryBuilder()
                    .where()
                    .eq(DBOpenerHelper.KEY_CATEGORIES_ID,
                            category.getCategoryID()).countOf();
            if (count > 0) {
                Categories categoryOld = dao
                        .queryBuilder()
                        .where()
                        .eq(DBOpenerHelper.KEY_CATEGORIES_ID,
                                category.getCategoryID()).queryForFirst();
                category.setIsActive(categoryOld.getIsActive());
                dao.update(category);
            } else {
                category.setIsActive(0);
                dao.create(category);
            }
        }
    }

    private static void deleteOldCategories(Context context,
                                            List<Categories> categoriesList) throws SQLException {

        Dao<Categories, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getCategoryDao();

        ArrayList<Integer> oldListIds = new ArrayList<>();
        ArrayList<Integer> newListIds = new ArrayList<>();
        ArrayList<Integer> uniqListIds = new ArrayList<>();

        for (Categories category : categoriesList) {
            newListIds.add(category.getCategoryID());
        }

        List<Categories> categoriesListOld = dao.queryForAll();
        for (Categories category : categoriesListOld) {
            oldListIds.add(category.getCategoryID());
        }

        for (Integer id : oldListIds) {
            if (!newListIds.contains(id))
                uniqListIds.add(id);
        }

        // dao.deleteIds(uniqListIds); not working
        for (Integer id : uniqListIds) {
            DeleteBuilder<Categories, Integer> deleteBuilder = dao
                    .deleteBuilder();
            deleteBuilder.where().eq(DBOpenerHelper.KEY_CATEGORIES_ID, id);
            deleteBuilder.delete();
        }

    }

    public static List<Categories> getCategoriesList(Context context)
            throws java.sql.SQLException {
        Dao<Categories, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getCategoryDao();

        Dao<News, Integer> daoNews = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getNewsDao();
        QueryBuilder<News, Integer> queryBuilder = daoNews.queryBuilder()
                .limit(1L);

        List<Categories> catList = dao.queryForAll();
        List<Categories> newcatList = new ArrayList<>();
        Categories catID0 = null;
        for (Categories categories : catList) {
            Where<News, Integer> where = queryBuilder.where();
            where.eq("category1", categories.getCategoryID());
            where.eq("category2", categories.getCategoryID());
            where.eq("category3", categories.getCategoryID());
            where.or(3);

            long count = where.countOf();
            if (count > 0 && categories.getCategoryID() > 0)
                newcatList.add(categories);
            if (count > 0 && categories.getCategoryID() == 0)
                catID0 = categories;
        }
        if (catID0 != null)
            newcatList.add(catID0);

        return newcatList;
    }

    public static Categories isCategoryExist(Context context, String key) {
        try {

            Dao<Categories, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getCategoryDao();

            long count = dao.queryBuilder().where().eq("en", key).countOf();
            if (count > 0) {
                return dao.queryBuilder().where().eq("en", key).queryForFirst();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static void updateCategory(Context context, Categories category) {
        try {
            Dao<Categories, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getCategoryDao();
            dao.update(category);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static int isAllCategoriesChecked(Context context) {
        try {
            Dao<Categories, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getCategoryDao();

            long count = dao.queryBuilder().where().eq("isActive", 1).countOf();
            if (count > 0) {
                long count2 = dao.queryBuilder().where().eq("isActive", 0)
                        .countOf();
                if (count2 > 0) {
                    return -1;
                } else
                    return 1;
            } else
                return 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public static List<Categories> getAllCategories(Context context,
                                                    boolean checked) {

        Dao<Categories, Integer> dao;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getCategoryDao();
            List<Categories> list = dao.queryBuilder().where()
                    .eq("isActive", checked == true ? 1 : 0).query();

            Categories category0 = null;
            for (Categories category : list) {
                if (category.getCategoryID() == 0) {
                    category0 = category;
                    list.remove(category);
                    break;
                }
            }
            if (category0 != null)
                list.add(category0);
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    public static String getCategoryById(Context context, int id) {

        Dao<Categories, Integer> dao;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getCategoryDao();
            return dao.queryBuilder().where().eq(DBOpenerHelper.KEY_CATEGORIES_ID, id).queryForFirst()
                    .getRu();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";

    }

    public static long getCategoryId(Context context, String name) {

        Dao<Categories, Integer> dao;
        try {
            dao = OpenHelperManager.getHelper(context, DatabaseHelper.class)
                    .getCategoryDao();
            return dao.queryBuilder().where().eq("en", name).queryForFirst()
                    .getCategoryID();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;

    }
}
