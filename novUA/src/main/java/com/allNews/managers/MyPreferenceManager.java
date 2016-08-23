package com.allNews.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.allNews.activity.AllNewsActivity;
import com.allNews.activity.Preferences;
import com.allNews.utils.GCMUtils;
import com.allNews.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gregory.network.rss.BuildConfig;
import gregory.network.rss.R;

public class MyPreferenceManager {
    public static final int VIEW_MODE_1 = 1;
    public static final int VIEW_MODE_2 = 2;
    public static final int VIEW_MODE_3 = 3;
    private static final String PREFS_GCM_NAME = "prefGCM";
    private static final String PREFS_EVENT_CATEGORY = "prefEventCategory";
    private static final String PREFS_EVENT_FILTER = "prefEventFilter";
    private static final String PREFS_HINTS = "prefHints";
    private static final String PREFS_NEWAPP = "prefNewApp";
    private static final String PREFS_HINTS_KEY = "hint";
    private static final String PREFS_NEWAPP_ID_KEY = "newApp_Id";
    private static final String PREFS_EVENT_ID_KEY = "event_Id";
    private static final String PREFS_VERSION_APP = "version_app";
    private static final String PREFS_CHANGELOG_IS_SHOWED = "changelog";

    public static void setNavigationItemToPrefence(Context context, int arg0) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Preferences.PREF_FEED, arg0);
        editor.apply();
    }

    public static int getNavigationItemFromPrefence(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context).getInt(
                Preferences.PREF_FEED, 0);
    }

    public static void saveCurrentPage(Context context, int arg0) {

        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putInt(Preferences.PREF_PAGE, arg0);
        editor.apply();

    }

    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = Utils.getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GCMUtils.PROPERTY_REG_ID, regId);
        editor.putInt(GCMUtils.PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = MyPreferenceManager
                .getGCMPreferences(context);
        String registrationId = prefs.getString(GCMUtils.PROPERTY_REG_ID, "");
        if (registrationId.equals("")) {

            return "";
        }

        int registeredVersion = prefs.getInt(GCMUtils.PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = Utils.getAppVersion(context);
        if (registeredVersion != currentVersion) {

            return "";
        }
        return registrationId;
    }

    public static SharedPreferences getGCMPreferences(Context context) {

        return context.getSharedPreferences(PREFS_GCM_NAME,
                Context.MODE_PRIVATE);
    }

    public static void setLaunchToday(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);

        String dateTime = sp.getString("LaunchToday", null);

        if (dateTime == null
                || (Utils.getTime(dateTime) > 0 && !Utils.getDate(dateTime)
                .equals(Utils.getCurrentDate()))) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("LaunchToday", Utils.getCurrentDateAndTime());
            editor.apply();
        }

    }

    public static boolean isTodayAppLaunch(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);

        String dateTime = sp.getString("LaunchToday", "");

        if (Utils.getTime(dateTime) > 0
                && (((System.currentTimeMillis() - Utils.getTime(dateTime)) > 30 * Utils.MINUTE))
                || !Utils.getDate(dateTime).equals(Utils.getCurrentDate())) {
            return true;
        }

        return false;
    }

    public static void disableEventMsg(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("eventMsgVisibility", false);

        editor.apply();
    }

    public static boolean showEventMsg(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);

        return sp.getBoolean("eventMsgVisibility", true);
    }

    public static boolean eventCategoryFilter(Context context, String category) {
        SharedPreferences prefs = getEventCategoryPreferences(context);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().equals(category))
                return (Boolean) entry.getValue();

        }
        setEventCategoryPreferences(context, category, true);

        return true;
    }

    public static boolean eventCategoryFilterClick(Context context,
                                                   String category) {
        boolean isCategorySelect = eventCategoryFilter(context, category);

        setEventCategoryPreferences(context, category, !isCategorySelect);

        return !isCategorySelect;
    }


    public static void setEventCategoryPreferences(Context context,
                                                   String category, boolean isSelected) {
        SharedPreferences prefs = getEventCategoryPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(category, isSelected);

        editor.apply();

    }

    public static void saveEventCityFilter(Context context, String str) {
        SharedPreferences prefs = getEventFilterPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("filterCity", str);

        editor.apply();

    }

    public static String getEventCityFilter(Context context) {
        SharedPreferences prefs = getEventFilterPreferences(context);

        return prefs.getString("filterCity", "");

    }

    public static List<String> getEventsCategoryFilter(Context context) {
        SharedPreferences prefs = getEventCategoryPreferences(context);

        Map<String, ?> allEntries = prefs.getAll();
        if (allEntries.size() == 0)
            return null;

        List<String> categoriesFilter = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if ((Boolean) entry.getValue())
                categoriesFilter.add(entry.getKey());

        }
        return categoriesFilter;

    }

    public static boolean isAllEventCategorySelected(Context context) {
        SharedPreferences prefs = getEventCategoryPreferences(context);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (!(Boolean) entry.getValue())
                return false;

        }

        return true;
    }

    public static void setAllEventCategorySelected(Context context,
                                                   boolean check) {
        SharedPreferences prefs = getEventCategoryPreferences(context);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            setEventCategoryPreferences(context, entry.getKey(), check);

        }

    }

    public static SharedPreferences getEventCategoryPreferences(Context context) {

        return context.getSharedPreferences(PREFS_EVENT_CATEGORY,
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getEventFilterPreferences(Context context) {

        return context.getSharedPreferences(PREFS_EVENT_FILTER,
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getNewAppPreferences(Context context) {

        return context.getSharedPreferences(PREFS_NEWAPP, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getHintsPreferences(Context context) {

        return context.getSharedPreferences(PREFS_HINTS, Context.MODE_PRIVATE);
    }

    public static void setThemeToPref(Context context, int curTheme) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (curTheme == AllNewsActivity.THEME_WHITE)
            curTheme = AllNewsActivity.THEME_DARK;
        else
            curTheme = AllNewsActivity.THEME_WHITE;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Preferences.PREF_THEME, curTheme);
        editor.apply();

    }

    public static int getCurrentTheme(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);

        return sp.getInt(Preferences.PREF_THEME, AllNewsActivity.THEME_WHITE);

    }

    public static int getHintPosition(Context context) {
        SharedPreferences prefs = getHintsPreferences(context);
        return prefs.getInt(PREFS_HINTS_KEY, 0);

    }

    public static void setHintNextPosition(Context context) {
        SharedPreferences prefs = getHintsPreferences(context);
        int currentPosition = prefs.getInt(PREFS_HINTS_KEY, -1);
        int maxPosition = context.getResources().getStringArray(R.array.hints).length;

        if (currentPosition < maxPosition)
            currentPosition++;
        else
            currentPosition = 0;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREFS_HINTS_KEY, currentPosition);
        editor.apply();
    }

    public static boolean getRotatePref(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getBoolean(Preferences.PREF_ORIENTATION, false);

    }

    public static int getNewAppId(Context context) {
        SharedPreferences sp = getNewAppPreferences(context);

        return sp.getInt(PREFS_NEWAPP_ID_KEY, 0);
    }

    public static void setNewAppId(Context context, int newAppId) {
        SharedPreferences sp = getNewAppPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREFS_NEWAPP_ID_KEY, newAppId);
        editor.apply();

    }
    public static long getEventId(Context context) {
        SharedPreferences sp = PreferenceManager
        .getDefaultSharedPreferences(context);
        return sp.getLong(PREFS_EVENT_ID_KEY, 0);
    }

    public static void setEventId(Context context, long eventId) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(PREFS_EVENT_ID_KEY, eventId);
        editor.apply();

    }

    public static int getTextSize(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        try {
            return Integer.parseInt(sp.getString(Preferences.PREF_SIZE, "16"));
        } catch (NumberFormatException e) {
            return 16;
        }
    }

    public static void setTextSize(Context context, String size) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Preferences.PREF_SIZE, size);
        editor.apply();

    }

    public static int getViewMode(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getInt(Preferences.PREF_VIEW_MODE, VIEW_MODE_1);
    }

    public static void setViewMode(Context context, int viewMode) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Preferences.PREF_VIEW_MODE, viewMode);
        editor.apply();

    }

    public static boolean isNewAppHasBest(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getBoolean(Preferences.PREF_BEST_NEWAPP, false);
    }

    public static void setNewAppHasBest(Context context, boolean hasBest) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Preferences.PREF_BEST_NEWAPP, hasBest);
        editor.apply();

    }

    public static void savePassToReadAllNewsPref(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        //	boolean adminMode = sp.getBoolean(Preferences.PREF_ADMIN, false);

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Preferences.PREF_FOR_READ_ALL_NEWS, true);
        editor.apply();

    }

    public static boolean getPassToReadAllNewsPref(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getBoolean(Preferences.PREF_FOR_READ_ALL_NEWS, false);


    }

    public static void saveLatLocation(Context context, double lat) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(Preferences.LATITUDE, Double.doubleToRawLongBits(lat));
        editor.apply();
    }

    public static void saveLonLocation(Context context, double lon) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(Preferences.lONGITUDE, Double.doubleToRawLongBits(lon));
        editor.apply();
    }

    public static double getSavedLatitude(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return Double.longBitsToDouble(sp.getLong(Preferences.LATITUDE, Double.doubleToRawLongBits(50.4506)));// default Kiev Latitude
    }

    public static double getSavedLongitude(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return Double.longBitsToDouble(sp.getLong(Preferences.lONGITUDE, Double.doubleToRawLongBits(30.5243)));// default Kiev Longitude
    }

    public static boolean needShowChangeLog(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String version = "";
        try {
            version = BuildConfig.VERSION_NAME;
        }catch (Exception e){
            e.printStackTrace();
        }

      if (!sp.getBoolean(PREFS_CHANGELOG_IS_SHOWED, false) || !sp.getString(PREFS_VERSION_APP, "").equals(version)){
          editor.putBoolean(PREFS_CHANGELOG_IS_SHOWED, true);
          editor.putString(PREFS_VERSION_APP, version);
          editor.apply();
          return true;
      }else return false;
    }

}

