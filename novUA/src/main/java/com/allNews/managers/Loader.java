package com.allNews.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.allNews.activity.Preferences;
import com.allNews.activity.TabFragment;
import com.allNews.application.App;
import com.allNews.data.Update;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

import gregory.network.rss.R;

public class Loader {
	public static final int UPDATE_STOP = 3;
	public static final int UPDATE_NEXT = 2;
	public static final int UPDATE_PROGRESS = 1;
	private Handler handlerUpdate;
	private Context context;

	RequestQueue requestQueue;

	public Loader(Context context, Handler handlerUpdate) {
		this.handlerUpdate = handlerUpdate;
		this.context = context;
		requestQueue = App.getRequestQueue();
		ManagerUpdates.removeOldUrl(context);
	}

	public void Start() {
		if (needUpdate()) {
			addTopRequest();
		}
	}

	public void StartSynch() {
		if (needUpdate())
			addAllRequests();
	}

	private boolean needUpdate() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (sp.getBoolean("checkboxWiFi", false)) {
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (!mWifi.isConnected()) {
				if (handlerUpdate != null)
					handlerUpdate.sendEmptyMessage(UPDATE_STOP);

				return false;
			}
		}

		return true;
	}

	private void addTopRequest() {
		JsonArrayRequest newsReq = ManagerNews.getTopNewsRequest(context,
				new Handler() {
					public void handleMessage(Message msg) {

						switch (msg.what) {
							case Loader.UPDATE_PROGRESS:

								break;
							case Loader.UPDATE_NEXT:
								updateProgressBar(20, true);
								addRequest();

								break;
							case Loader.UPDATE_STOP:
								addRequest();

								break;
							default:
								break;
						}
					}

					;
				});
		newsReq.setRetryPolicy(new DefaultRetryPolicy(20000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		requestQueue.add(newsReq);
		JsonArrayRequest b2bReq = ManagerNews.getB2BNewsRequest(context);
		JsonArrayRequest likesReq = ManagerNews.getLikeRequest(context);
		requestQueue.add(likesReq);
		requestQueue.add(b2bReq);

	}

	private void addRequest() {
		Update update = ManagerUpdates.getLastUpdateUrl(context);
		if (update != null && update.getUpdateUrl() != null
				&& update.getUpdateUrl().length() > 0) {
			final String url = update.getUpdateUrl();

			JsonArrayRequest newsReq = ManagerNews.getNewsRequest(url, context,
					new Handler() {
						public void handleMessage(Message msg) {

							switch (msg.what) {
							case Loader.UPDATE_PROGRESS:
								updateProgressBar(msg.arg1, false);

								break;
							case Loader.UPDATE_NEXT:

								addRequest();

								break;
							case Loader.UPDATE_STOP:
								stopUpdate();

								break;
							default:
								break;
							}
						};
					});
			newsReq.setRetryPolicy(new DefaultRetryPolicy(20000,
					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

			requestQueue.add(newsReq);
		} else
			stopUpdate();

	}

	private void addAllRequests() {

		JsonArrayRequest newsReq = ManagerNews.getTopNewsRequest(context, null);
		newsReq.setRetryPolicy(new DefaultRetryPolicy(20000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		requestQueue.add(newsReq);

		Update update = ManagerUpdates.getLastUpdateUrl(context);
		while (update != null) {

			final String url = update.getUpdateUrl();
			newsReq = ManagerNews.getNewsRequest(url, context, null);
			newsReq.setRetryPolicy(new DefaultRetryPolicy(20000,
					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

			requestQueue.add(newsReq);
			ManagerUpdates.removeurl(context, url);
			update = ManagerUpdates.getLastUpdateUrl(context);

		}
		JsonArrayRequest b2bReq = ManagerNews.getB2BNewsRequest(context);
		JsonArrayRequest likesReq = ManagerNews.getLikeRequest(context);
		if (!context.getResources().getBoolean(R.bool.need_event)) {
			JsonArrayRequest articlesReq = ManagerNews.getArticlesRequest(context);
			requestQueue.add(articlesReq);
		}
		requestQueue.add(likesReq);
		requestQueue.add(b2bReq);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		if (sp.getBoolean(Preferences.PREF_NOTIFICATION, true))
		ManagerApp.showNotification(context, ManagerNews.getTopNewsForNotification(context));

	}

	private void updateProgressBar(int length, boolean itsTop) {
		if (handlerUpdate != null) {
			Message msg = new Message();
			msg.what = Loader.UPDATE_PROGRESS;
			if (itsTop)
				msg.obj = TabFragment.TAB_TOP;
			msg.arg1 = length;
			handlerUpdate.sendMessage(msg);
		}

	}

	protected void stopUpdate() {

		if (handlerUpdate != null)
			handlerUpdate.sendEmptyMessage(Loader.UPDATE_STOP);
	}

}
