package com.allNews.managers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.allNews.activity.AllNewsActivity;
import com.allNews.activity.Preferences;
import com.allNews.application.App;
import com.allNews.data.News;
import com.allNews.db.DatabaseHelper;
import com.allNews.web.Requests;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Target;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import gregory.network.rss.R;

public class ManagerApp {
    private static Target target;

    private static int NOTIFY_ID = 101;

    public static void startPeriodicUpdate(final Context context) {
        // the user needs time to choose the sources
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(context);
                long SYNC_INTERVAL = Long.parseLong(sp.getString(
                        Preferences.PREF_UPDATE, "3600"));

                String AUTHORITY = context.getResources().getString(
                        R.string.app_authority);
                String ACCOUNT_TYPE = context.getResources().getString(
                        R.string.app_account_type);
                String ACCOUNT = context.getResources().getString(
                        R.string.app_account);

                Account mAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
                AccountManager accountManager = (AccountManager) context
                        .getSystemService(Context.ACCOUNT_SERVICE);

                accountManager.addAccountExplicitly(mAccount, null, null);

                if (SYNC_INTERVAL == 0) {
                    ContentResolver.removePeriodicSync(mAccount, AUTHORITY,
                            Bundle.EMPTY);
                    return;
                }
                // SYNC_INTERVAL =90L;

                ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
                ContentResolver.addPeriodicSync(mAccount, AUTHORITY,
                        Bundle.EMPTY, SYNC_INTERVAL);
            }
        }, ManagerUpdates.MINUTE);

    }

    public static JsonArrayRequest getCategoriesAndSourcesRequest(
            final Context context, final Handler handler) {
        String url = context.getResources().getString(R.string.url_base)
                + context.getResources().getString(
                R.string.url_get_categorieswithsources)
                + context.getResources().getString(R.string.app_id);
        return Requests.getRequest(url, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                saveCategoriesAndSourcesInThread(context, response, handler);

            }
        }, null);

    }

    public static void saveCategoriesAndSourcesInThread(final Context context,
                                                        final JSONArray response, final Handler handler) {
        new Thread(new Runnable() {
            @SuppressLint("LongLogTag")
            public void run() {
                try {

                    JSONArray categories = response.getJSONArray(0);
                    JSONArray sources = response.getJSONArray(1);

                    ManagerCategories.saveCategories(context, categories);
                    ManagerSources.saveSources(context, sources);

                } catch (Exception e) {
                    Log.e("saveCategoriesAndSourcesInThread", "Exception " + e);
                }
                handler.sendEmptyMessage(1);
            }
        }).start();

    }

    public static JsonArrayRequest sendPush(Context context, int newsId) {
        String url = context.getResources().getString(R.string.url_domain)
                + context.getResources().getString(R.string.send_push_url)
                //+ context.getResources().getString(R.string.send_push_url_test)
                + context.getResources().getString(R.string.app_id) + "&news="
                + newsId;
        // Log.e("url",url);
        return Requests.getRequest(url, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.e("sendPush", "response " + response);

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("sendPush", "error  " + error);

            }
        });
    }

    public static void tryCopyDb(final Context context) {
        try {
            InputStream myInput = context.getAssets().open(
                    DatabaseHelper.DATABASE_NAME);
            String dbPath = DatabaseHelper.DB_PATH;
            if (dbPath.equals(""))
                dbPath = context.getFilesDir().getAbsolutePath()
                        + "/../databases/";
            if (!new File(dbPath).exists())
                new File(dbPath).mkdirs();

            String outFileName = dbPath + DatabaseHelper.DATABASE_NAME;
            //	if (!new File(outFileName).exists()) {
            OutputStream myOutput = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();

            //	}
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



   /* public static void loadNotification(List<News> newses, final Context context){

        if (newses.size() != 3){
            newses = ManagerNews.getTopNewsForNotification(context);
        }

        final List<News> finalNewses = newses;
        if(finalNewses.size() == 0){
            return;
        }
        target =new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            createNotification(context, finalNewses,  bitmap, NOTIFY_ID );
                Toast.makeText(context, "BitmapLoaded", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Toast.makeText(context , "BitmapFailed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

                    Toast.makeText(context, "PrepereLoad", Toast.LENGTH_LONG).show();


            }
        };
        int size = context.getResources().getDisplayMetrics().widthPixels ;
        for (int i =0; i< 3; i++) {
            NOTIFY_ID = i;
            EWLoader.with(context).load(newses.get(i).getImageUrl()).into(target);

        }


    }*/

    public static void showNotification(final Context context, List<News> newses) {
        if (newses.size() != 5) {
            return;
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.top5);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(context.getResources().getString(R.string.notification_content_title))
                .setContentText("1. " + newses.get(0).getTitle())
                .setLargeIcon(largeIcon);
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        Intent intent = new Intent(context.getApplicationContext(), AllNewsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("OPEN_TAB_TOP");
        PendingIntent intentPanding = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intentPanding);
        if (android.os.Build.VERSION.SDK_INT >= 16) {

            for (int i = 0; i < newses.size(); i++) {
                inboxStyle.addLine(i + 1 + ". " + newses.get(i).getTitle());

            }
            inboxStyle.addLine(newses.size() + 1 + " .  ...");
            inboxStyle.setBigContentTitle(context.getResources().getString(R.string.notification_big_title))
                    .setSummaryText(context.getResources().getString(R.string.notification_summary));
            mBuilder.setStyle(inboxStyle);
        }
        mBuilder.setAutoCancel(true);
        Notification notification = mBuilder.build();
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (newses.size() > 0 && !App.isActivityVisible())
            nm.notify(NOTIFY_ID, notification);
    }

    public static void removeNotification(Context context) {

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) context.getSystemService(ns);
        mNotificationManager.cancel(NOTIFY_ID);

    }


   /* public static void createNotification(Context context, List<News> newses, Bitmap bitmap, int notifyID) {
        // BEGIN_INCLUDE(notificationCompat)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // END_INCLUDE(notificationCompat)

        // BEGIN_INCLUDE(intent)
        //Create Intent to launch this Activity again if the notification is clicked.
        Intent i = new Intent(context.getApplicationContext(), AllNewsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, notifyID, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        // END_INCLUDE(intent)

        // BEGIN_INCLUDE(ticker)
        // Sets the ticker text
        builder.setTicker("Свіжі новини");

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.ic_notif);
        builder.setContentText("Bla bla bla");

        builder.setAutoCancel(true);

        // Build the notification
        Notification notification = builder.build();
        // END_INCLUDE(buildNotification)

        // BEGIN_INCLUDE(customLayout)
        // Inflate the notification layout as RemoteViews
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);

        // Set text on a TextView in the RemoteViews programmatically.
        final String time = DateFormat.getTimeInstance().format(new Date()).toString();
        final String text = newses.get(notifyID).getTitle();
        contentView.setTextViewText(R.id.textViewNoty, text);

      *//*  *//**//**//**//* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         *//**//**//**//**//*
        notification.contentView = contentView;

        // Add a big content view to the notification if supported.
        // Support for expanded notifications was added in API level 16.
        // (The normal contentView is shown when the notification is collapsed, when expanded the
        // big content view set here is displayed.)
        if (Build.VERSION.SDK_INT >= 16) {
            // Inflate and set the layout for the expanded notification view
            RemoteViews expandedView =
                    new RemoteViews(context.getPackageName(), R.layout.notification_expanded);
            expandedView.setImageViewBitmap(R.id.listItemImg1, bitmap);
            expandedView.setTextViewText(R.id.listItemTitle1 ,newses.get(notifyID).getSource());
            expandedView.setTextViewText(R.id.listItemSource1, newses.get(notifyID).getTitle());
            notification.bigContentView = expandedView;
        }
        // END_INCLUDE(customLayout)
        // START_INCLUDE(notify)
        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notifyID, notification);
        // END_INCLUDE(notify)
    }*/


}
