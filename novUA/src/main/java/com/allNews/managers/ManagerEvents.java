package com.allNews.managers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.allNews.data.Event;
import com.allNews.db.DatabaseHelper;
import com.allNews.utils.GsonUtils;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManagerEvents {

    public static JsonArrayRequest getSourcesRequest(final Context context,
                                                     final Handler handler) {

        return Requests.getJsonRequestForEvents(context,
                new Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        List<Event> result = new ArrayList<Event>();
                        Type listType = new TypeToken<List<Event>>() {
                        }.getType();

                        result = GsonUtils.getGson().fromJson(
                                response.toString(), listType);
                        saveEventInThread(context, result, handler);

                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(" ManagerEvents error", "" + error);
                        handler.sendEmptyMessage(1);
                    }

                });

    }

    protected static void saveEventInThread(final Context context,
                                            final List<Event> result, final Handler handler) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    saveEvent(context, result);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    handler.sendEmptyMessage(1);
                }
            }
        }).start();

    }

    private static void saveEvent(Context context, List<Event> eventList)
            throws SQLException {

        Dao<Event, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getEventDao();

        DeleteBuilder<Event, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.delete();

        int size = eventList != null ? eventList.size() : 0;
        for (int index = 0; index < size; index++) {
            Event event = eventList.get(index);
            if (event.getMap() != null) {
                event.setMapLat(event.getMap().getMapLat());
                event.setMapLng(event.getMap().getMapLng());
            }

            if (event.getCity() != null)
                event.setCityIndex(event.getCity().toLowerCase());

            if (event.getNameEn() != null)
                event.setNameEnIndex(event.getNameEn().toLowerCase());

            if (event.getNameRu() != null)
                event.setNameRuIndex(event.getNameRu().toLowerCase());

            if (event.getNameUa() != null)
                event.setNameUaIndex(event.getNameUa().toLowerCase());

            dao.create(event);

        }
    }

    public static List<Event> getEvents(Context context, boolean top,
                                        String filterFrom, String filterTo) throws SQLException {
        Dao<Event, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getEventDao();
        QueryBuilder<Event, Integer> queryBuilder = dao.queryBuilder().orderBy(
                "whenStart", true);

        Where<Event, Integer> where = queryBuilder.where().eq("is_top", top);

        if (filterFrom != null && !filterFrom.isEmpty()) {
            try {
                filterFrom = filterFrom + " 00:00";
                Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK)
                        .parse(filterFrom);
                long from = date.getTime() / 1000;
                where.and().ge("whenStart", from);
            } catch (ParseException ignored) {

            }
        }

        if (filterTo != null && !filterTo.isEmpty()) {
            try {
                filterTo = filterTo + " 23:59";
                Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK)
                        .parse(filterTo);
                long to = date.getTime() / 1000;
                where.and().le("whenStart", to);
            } catch (ParseException ignored) {

            }
        }
        String filterCity = "";
        try {
            filterCity = MyPreferenceManager.getEventCityFilter(context)
                    .toLowerCase();
        } catch (Exception e) {
            return where.query();
        }
        if (!filterCity.isEmpty()) {
            where.and().eq("cityIndex", filterCity);

        }
        List<String> categoriesFilter = MyPreferenceManager
                .getEventsCategoryFilter(context);
        if (categoriesFilter != null) {
            where.and().in("category", categoriesFilter);
        }
        return where.query();
    }
    public static List<Event> getEventsByCountry(Context context, boolean top,
                                        String filterFrom, String filterTo, String country) throws SQLException {
        Dao<Event, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getEventDao();
        QueryBuilder<Event, Integer> queryBuilder = dao.queryBuilder().orderBy(
                "whenStart", true);

        Where<Event, Integer> where = queryBuilder.where().eq("is_top", top).and().eq("country", country);

        if (filterFrom != null && !filterFrom.isEmpty()) {
            try {
                filterFrom = filterFrom + " 00:00";
                Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK)
                        .parse(filterFrom);
                long from = date.getTime() / 1000;
                where.and().ge("whenStart", from);
            } catch (ParseException ignored) {

            }
        }

        if (filterTo != null && !filterTo.isEmpty()) {
            try {
                filterTo = filterTo + " 23:59";
                Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK)
                        .parse(filterTo);
                long to = date.getTime() / 1000;
                where.and().le("whenStart", to);
            } catch (ParseException ignored) {

            }
        }
        String filterCity = "";
        try {
            filterCity = MyPreferenceManager.getEventCityFilter(context)
                    .toLowerCase();
        } catch (Exception e) {
            return where.query();
        }
        if (!filterCity.isEmpty()) {
            where.and().eq("cityIndex", filterCity);

        }
        List<String> categoriesFilter = MyPreferenceManager
                .getEventsCategoryFilter(context);
        if (categoriesFilter != null) {
            where.and().in("category", categoriesFilter);
        }
        return where.query();
    }

    public static Event getEventById(Context context, Long eventID)
            throws SQLException {
        Dao<Event, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getEventDao();

        return dao.queryBuilder().where().eq("event_id", eventID)
                .queryForFirst();
    }

    public static List<Event> getEvents(Context context, String str) {
        try {
            Dao<Event, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getEventDao();

            QueryBuilder<Event, Integer> qb = dao.queryBuilder();
            // qb.where().like("madeCompany", "%" + str + "%");
            qb.where().like("cityIndex", str + "%").or()
                    .like("name_en_index", "%" + str + "%").or()
                    .like("name_ru_index", "%" + str + "%").or()
                    .like("name_ua_index", "%" + str + "%");
            PreparedQuery<Event> pq = qb.prepare();
            return dao.query(pq);
        } catch (SQLException e) {
            return new ArrayList<Event>();
        }
    }

    public static List<Event> getAllEventsFromDb(Context context) {
        List<Event> values = new ArrayList<Event>();

        try {
            List<Event> ev1 = ManagerEvents
                    .getEvents(context, true, null, null);
            List<Event> ev2 = ManagerEvents.getEvents(context, false, null,
                    null);
            if (ev1 != null && !ev1.isEmpty())
                values.addAll(ev1);
            if (ev2 != null && !ev2.isEmpty())
                values.addAll(ev2);
        } catch (SQLException e) {
            return values;
        }
        return values;
    }

    public static List<Event> getAllEventsFromDb(Context context,
                                                 String filterFrom, String filterTo) {
        List<Event> values = new ArrayList<Event>();

        try {
            List<Event> ev1 = ManagerEvents.getEvents(context, true,
                    filterFrom, filterTo);
            List<Event> ev2 = ManagerEvents.getEvents(context, false,
                    filterFrom, filterTo);
            if (ev1 != null && !ev1.isEmpty())
                values.addAll(ev1);
            if (ev2 != null && !ev2.isEmpty())
                values.addAll(ev2);
        } catch (SQLException e) {
            return values;
        }
        return values;
    }

    public static List<Event> getEventsWithUniqCategory(Context context)
            throws SQLException {
        Dao<Event, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getEventDao();
        QueryBuilder<Event, Integer> queryBuilder = dao.queryBuilder();

        return queryBuilder.distinct().selectColumns("category").query();

    }

    public static List<CharSequence> getEventCity(Context context, String str)
            throws SQLException {
        Dao<Event, Integer> dao = OpenHelperManager.getHelper(context,
                DatabaseHelper.class).getEventDao();
        QueryBuilder<Event, Integer> qb = dao.queryBuilder();

        qb.distinct().selectColumns("city").where()
                .like("cityIndex", str + "%");
        List<Event> events = dao.query(qb.prepare());

        List<CharSequence> cities = new ArrayList<CharSequence>();
        for (Event event : events) {
            cities.add(event.getCity());
        }
        return cities;
    }



    public static boolean isEventExist(Context context) {
        try {
            Dao<Event, Integer> dao = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class).getEventDao();
            return dao.isTableExists();
        } catch (SQLException e) {
            return false;
        }

    }
    public static Event getEventForNewsList(Context context, int position, String country) throws SQLException {
        int index = position/20;
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        List<Event> topEvents = getEventsByCountry(context, true, date, null, country);
        long eventId = MyPreferenceManager.getEventId(context);
        if (eventId == 0 && !topEvents.isEmpty()) {
            MyPreferenceManager.setEventId(context, topEvents.get(0)
                    .getEventId());
            return topEvents.get(0);
        }

        for (int i = 0; i < topEvents.size(); i++) {
            if (eventId == topEvents.get(i).getEventId()) {
                Event nextEvent = null;
                if (topEvents.size() - 1 == i) {
                    nextEvent = topEvents.get(0);
                } else {
                    nextEvent = topEvents.get(i + 1);
                }
                if(topEvents.size() > index){
                    nextEvent = topEvents.get(index);
                }
                MyPreferenceManager
                        .setEventId(context, nextEvent.getEventId());
                return nextEvent;
            }
        }
        if (!topEvents.isEmpty()) {
            MyPreferenceManager.setEventId(context,topEvents.get(0).getEventId());
            return topEvents.get(0);
        }else {
            return null;
        }

    }

}
