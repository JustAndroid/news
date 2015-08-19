package com.allNews.service;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.allNews.managers.Loader;
import com.allNews.managers.ManagerMessages;
import com.allNews.managers.ManagerUpdates;
import com.allNews.utils.Utils;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	// Global variables
	// Define a variable to contain a content resolver instance
    final ContentResolver mContentResolver;
	final Context context;

	/**
	 * Set up the sync adapter
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	 

		this.context = context;
		mContentResolver = context.getContentResolver();
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SyncAdapter(
			Context context,
			boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
		this.context = context;
		mContentResolver = context.getContentResolver();

	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.e("onPerformSync", "onPerformSync");

		if (Utils.checkInternetConnection(context, false)) {

			new ManagerMessages(context);
			ManagerUpdates.getListOfUrl(context, null);

			new Loader(context, null).StartSynch();
		}

	}

}
