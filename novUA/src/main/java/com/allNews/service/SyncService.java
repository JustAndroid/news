package com.allNews.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


public class SyncService extends Service {
	// Storage for an instance of the sync adapter
	private static SyncAdapter sSyncAdapter = null;
	// Object to use as a thread-safe lock
	private static final Object sSyncAdapterLock = new Object();

	/*
	 * Instantiate the sync adapter object.
	 */
	@Override
	public void onCreate() {

		/*
		 * Create the sync adapter as a singleton. Set the sync adapter as
		 * syncable Disallow parallel syncs
		 */
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null) {
				if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
					sSyncAdapter = new SyncAdapter(getApplicationContext(), true, false);
					Log.e("onPerformSync", "Create new SyncAdapter ");
				}else {
					sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
					Log.e("onPerformSync", "Create old SyncAdapter ");
				}
			}
		}
	}

	/**
	 * Return an object that allows the system to invoke the sync adapter.
	 *
	 */
	@Override
	public IBinder onBind(Intent intent) {
		/*
		 * Get the object that allows external processes to call
		 * onPerformSync(). The object is created in the base class code when
		 * the SyncAdapter constructors call super()
		 */
		return sSyncAdapter.getSyncAdapterBinder();
	}

}
