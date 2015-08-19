package com.allNews.managers;

import android.content.Context;
import android.util.Log;

import com.allNews.activity.Preferences;
import com.allNews.data.Source;
import com.allNews.db.DBOpenerHelper;
import com.allNews.db.DatabaseHelper;
import com.allNews.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManagerSources {

    //	public static JsonArrayRequest getSourcesRequest(final Context context,
//			final Handler handler) {
//		String urlSource = context.getResources().getString(R.string.url_base)
//				+ context.getResources().getString(R.string.url_get_sources);
//		return Requests.getRequest(urlSource, new Listener<JSONArray>() {
//
//			@Override
//			public void onResponse(JSONArray response) {
//				// Log.e("ManagerSources response", "" + response);
//
//				saveInThread(context, response);
//
//				handler.sendEmptyMessage(1);
//			}
//		}, new ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				Log.e(" ManagerSources error", "" + error);
//				 
//			}
//		});
//	}
//
//	public static void saveInThread(final Context context,
//			final JSONArray response) {
//		new Thread(new Runnable() {
//			public void run() {
//				try {
//					List<Source> result = new ArrayList<Source>();
//					Type listType = new TypeToken<List<Source>>() {
//					}.getType();
//					result = GsonUtils.getGson().fromJson(response.toString(),
//							listType);
//					saveOrUpdateSources(context, result);
//					deleteOldSources(context, result);
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}).start();
//
//	}
    public static void saveSources(final Context context,
                                   final JSONArray response) {

        try {
            List<Source> result = new ArrayList<>();
            Type listType = new TypeToken<List<Source>>() {
            }.getType();
            result = GsonUtils.getGson().fromJson(response.toString(),
                    listType);
            saveOrUpdateSources(context, result);
            deleteOldSources(context, result);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private static void saveOrUpdateSources(Context context,
                                            List<Source> sourcesList) throws SQLException {
        Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getSourceDao();
        int size = sourcesList != null ? sourcesList.size() : 0;
        for (int index = 0; index < size; index++) {
            Source source = sourcesList.get(index);
            long count = dao.queryBuilder().where()
                    .eq(DBOpenerHelper.KEY_SOURCE_ID, source.getSourceID())
                    .countOf();
            if (count > 0) {
                Source sourceOld = dao.queryBuilder().where()
                        .eq(DBOpenerHelper.KEY_SOURCE_ID, source.getSourceID())
                        .queryForFirst();
                if (source.getSourceID() == 175){
                    source.setName("Хартия 97");
                    dao.update(source);
                }
                //	source.setIsActive(sourceOld.getIsActive());
                //	int t=	dao.update(source);
                UpdateBuilder<Source, Integer> updateBuilder = dao.updateBuilder();
                updateBuilder.where().eq(DBOpenerHelper.KEY_SOURCE_ID, source.getSourceID());
                updateBuilder.updateColumnValue("app", source.getApp());
                updateBuilder.updateColumnValue(DBOpenerHelper.KEY_SOURCE_URL, source.getUrl());
                updateBuilder.updateColumnValue(DBOpenerHelper.KEY_SOURCE_NAME, source.getName());
                updateBuilder.updateColumnValue(DBOpenerHelper.KEY_SOURCE_IS_ACTIVE, sourceOld.getIsActive());
                updateBuilder.updateColumnValue(DBOpenerHelper.KEY_SOURCE_ENABLED, source.getEnabled());
                updateBuilder.updateColumnValue(DBOpenerHelper.KEY_SOURCE_DEFAULT, source.getDefault());
                try {
                    updateBuilder.update();
                }catch (Exception e){
                    Log.e("SourcesSaveError", e.toString() );
                }


            } else {
                setActiveFlag(source);
                dao.create(source);
            }
        }
    }

    private static void deleteOldSources(Context context,
                                         List<Source> sourcesList) throws SQLException {

        Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getSourceDao();

        ArrayList<Integer> oldListIds = new ArrayList<>();
        ArrayList<Integer> newListIds = new ArrayList<>();
        ArrayList<Integer> uniqListIds = new ArrayList<>();

        for (Source source : sourcesList) {
            newListIds.add(source.getSourceID());
        }

        List<Source> sourcesListOld = dao.queryForAll();
        for (Source source : sourcesListOld) {
            oldListIds.add(source.getSourceID());
        }

        for (Integer id : oldListIds) {
            if (!newListIds.contains(id))
                uniqListIds.add(id);
        }

        for (Integer id : uniqListIds) {
            DeleteBuilder<Source, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq(DBOpenerHelper.KEY_SOURCE_ID, id);
            deleteBuilder.delete();
        }

    }

    private static void setActiveFlag(Source source) {

        if (source.getDefault() == 1) {
            String defLang = Locale.getDefault().getLanguage();
            String sourceName = source.getName();
            int activeFlag = source.getDefault();

      /*      if (sourceName.toLowerCase().contains("(uk)")
                    || sourceName.toLowerCase().contains("(ua)")) {
                if (defLang.equals(Preferences.LANGUAGE_UA))
                    activeFlag = 1;
                else
                    activeFlag = 0;

            } else if (sourceName.toLowerCase().contains("(ru)")) {
                if (defLang.equals(Preferences.LANGUAGE_UA))
                    activeFlag = 0;
                else
                    activeFlag = 1;
            }*/
            source.setIsActive(activeFlag);

        } else
            source.setIsActive(0);

    }

    public static Source isSourceExist(Context context, String sourceID) {

        try {
            Integer.parseInt(sourceID);

            Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getSourceDao();

            long count = dao.queryBuilder().where()
                    .eq(DBOpenerHelper.KEY_SOURCE_ID, sourceID).countOf();
            if (count > 0) {
                return dao.queryBuilder().where()
                        .eq(DBOpenerHelper.KEY_SOURCE_ID, sourceID)
                        .queryForFirst();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static List<Source> getAllSources(Context context, boolean checked)
            throws SQLException {

        Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getSourceDao();
        if (!checked) {
            String defLang = Locale.getDefault().getLanguage();
            String key = "(UA)";
            if (defLang.equals(Preferences.LANGUAGE_UA))
                key = "(RU)";

            return dao
                    .queryBuilder()
                    .where()
                    .eq(DBOpenerHelper.KEY_SOURCE_IS_ACTIVE,
                            checked == true ? 1 : 0).and().not()
                    .like("name", "%" + key).query();
        } else

            return dao
                    .queryBuilder()
                    .where()
                    .eq(DBOpenerHelper.KEY_SOURCE_IS_ACTIVE,
                            checked == true ? 1 : 0).query();
    }

    public static void updateSource(final Context context, final Source source) {

        try {
            Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getSourceDao();
            dao.update(source);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static int isAllSourcesChecked(Context context) {

        try {
            Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getSourceDao();
            long count = dao.queryBuilder().where()
                    .eq(DBOpenerHelper.KEY_SOURCE_IS_ACTIVE, 1).countOf();
            if (count > 0) {
                long count2 = dao.queryBuilder().where()
                        .eq(DBOpenerHelper.KEY_SOURCE_IS_ACTIVE, 0).countOf();
                if (count2 > 0) {
                    return -1;
                } else
                    return 1;
            } else
                return 0;
        } catch (Exception e) {
            return 0;
        }

    }

    public static String getFormattingCheckedSourcesCount(Context context) {
        try {
            Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getSourceDao();

            long allSources = dao.countOf();
            long checkedSources = dao.queryBuilder().where()
                    .eq(DBOpenerHelper.KEY_SOURCE_IS_ACTIVE, 1).countOf();
            return " (" + checkedSources + "/" + allSources + ")";
        } catch (SQLException e) {
            return "";
        }

    }

    public static String getCheckedSourcesCount(Context context) {
        try {
            Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getSourceDao();


            return "" + dao.queryBuilder().where()
                    .eq(DBOpenerHelper.KEY_SOURCE_IS_ACTIVE, 1).countOf();

        } catch (SQLException e) {
            return "";
        }

    }

    public static String getAllSourcesCount(Context context) {
        try {
            Dao<Source, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getSourceDao();

            return "" + dao.countOf();

        } catch (SQLException e) {
            return "";
        }

    }

    public static Source getSourceById(Context context, int id) {
        Source source =null;
        try {
            Dao<Source, Integer> dao = OpenHelperManager.getHelper(
                    context, DatabaseHelper.class).getSourceDao();
            source = dao
                    .queryBuilder()
                    .where()
                    .eq(DBOpenerHelper.KEY_SOURCE_ID,
                            id).queryForFirst();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return source;

    }
   public static List<Integer> getExcludedSourcesId (){
       ArrayList<Integer> sourcesIds = new ArrayList<>();
       sourcesIds.add(184);//BBC Ukraine
        sourcesIds.add(88); // Censor
        sourcesIds.add(148); //UKR Pravda (UA)
        sourcesIds.add(149); //UKR Pravda (RU)
        sourcesIds.add(27); //EKONOM Pravda (UA)
        sourcesIds.add(28); //EKONOM Pravda (RU)


       return sourcesIds;
   }

}
