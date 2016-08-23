package com.allNews.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.allNews.activity.AllNewsActivity;
import com.allNews.activity.Preferences;
import com.allNews.utils.Utils;
import com.allNews.web.HttpCommunicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gregory.network.rss.R;

public class ManagerMessages {

    private final Context context;

    public ManagerMessages(Context context) {
        this.context = context;
        checkServerMsg();
    }

    private void checkServerMsg() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        final String msgTime = sp.getString(Preferences.PREF_MESSAGE_TIME,
                Utils.getCurrentTime());// "2014-04-15%2017:33:15";//sp.getString(Preferences.PREF_MESSAGE_TIME,
        // getCurrentTime());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Preferences.PREF_MESSAGE_TIME, Utils.getCurrentTime());
        editor.apply();

        HttpCommunicator mHttpCommunicator = new HttpCommunicator(context);

        String url = context.getResources().getString(R.string.url_base)
                + context.getResources().getString(
                R.string.url_get_messages) + msgTime;
        String response = mHttpCommunicator.sendGetRequest(url);
        if (response != null && !response.equals("")) {

            try {
                JSONArray dataArray = new JSONArray(response);
                // Log.e("dataArray " +dataArray);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject data = dataArray.getJSONObject(i);
                    sendNotif(i, data.optString("text"));
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }


        // Log.e("msgTime  " + msgTime);

    }

    private void sendNotif(int id, String text) {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(context.NOTIFICATION_SERVICE);
//        Notification notif = new Notification(R.drawable.ic_launcher, "",
//                System.currentTimeMillis());
//
        Intent intent = new Intent(context, AllNewsActivity.class);

        PendingIntent pIntent = PendingIntent
                .getActivity(context, 0, intent, 0);
//
//        notif.setLatestEventInfo(context,
//                context.getString(R.string.notif_title), text, pIntent);
//
//        notif.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        nm.notify(id, notif);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notif = builder.setContentIntent(pIntent)
                .setSmallIcon(R.drawable.ic_launcher)
//                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.notif_title))
                .setContentText(text)
                .build();
        nm.notify(id, notif);

    }


}
