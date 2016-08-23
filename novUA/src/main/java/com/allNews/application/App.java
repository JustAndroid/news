package com.allNews.application;


import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.allNews.client.ServerController;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class App extends Application {

    private static boolean activityVisible;

    //    private static Context mContext;
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        ServerController.initializeVolley(this);

//        mContext = this;
        // LeakCanary.install(this);
    }

//    public static Context getContext() {
//        return mContext.getApplicationContext();
//    }

    public static RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }
    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}


