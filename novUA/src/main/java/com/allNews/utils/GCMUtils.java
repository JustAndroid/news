package com.allNews.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.allNews.application.App;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.web.Requests;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import gregory.network.rss.R;

public class GCMUtils {
	private static final String TAG = "GCMUtils";
	public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String PROPERTY_APP_VERSION = "appVersion";

	public static void itinGCM(Context context) {

		if (GCMUtils.checkPlayServices(context)) {

			String regid = MyPreferenceManager.getRegistrationId(context);

			if (regid.isEmpty())
				GCMUtils.tryRegisterInBackground(context);

		}

	}
	
	public static boolean checkPlayServices(Context context) {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode,
						(Activity) context, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i(TAG, "This device is not supported.");
				// ((Activity) context).finish();
				return false;
			}
			return false;
		}
		return true;
	}

	public static void tryRegisterInBackground(final Context context) {

		Utils.isOnline(context, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					registerInBackground(App.getContext());
					break;

				default:
				 
					break;
				}

			}
		});


	}
	public static void registerInBackground(final Context context) {

		 
		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				GoogleCloudMessaging gcm = GoogleCloudMessaging
						.getInstance(context);

				try {
					String regid = gcm.register(context.getResources()
							.getString(R.string.project_number));
					registerInServer(context, handler, regid);
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		};
		new Thread(runnable).start();

	}
	protected static void registerInServer(final Context context,
			Handler handler, final String regid) {
		handler.post(new Runnable() {
			@Override
			public void run() {

				RequestQueue requestQueue = App.getRequestQueue();
				if (regid != null && !regid.equals("")) {
					requestQueue.add(getRegisterGCMRequest(context, regid));

				}

			}

		});

	}

	private static Request getRegisterGCMRequest(final Context context,
			final String regid) {

		String url = context.getResources().getString(
				R.string.url_domain)
				+context.getResources().getString(
				R.string.register_push_url)
				+ "&set_id="
				+ context.getResources().getString(R.string.app_id)
				+ "&key="
				+ regid + "&device_id=" + Utils.getDeviceId(context);

		return Requests.getStringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				MyPreferenceManager.storeRegistrationId(context, regid);

			}
		}, null);
	}
}