package com.allNews.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.allNews.activity.AllNewsActivity;
import com.allNews.activity.NewsCollectionActivity;
import com.allNews.utils.Utils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import gregory.network.rss.R;

public class GcmIntentService extends IntentService {
	public static int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
        String TAG = "GcmIntentService";
        Log.e(TAG, "Received: " + extras.toString());
		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				// sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				// sendNotification("Deleted messages on server: "
				// + extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				if (extras.containsKey("message")) {
					Intent intentNotif = new Intent(this, AllNewsActivity.class);
					sendNotification(extras.getString("message"), intentNotif);
				} else if (extras.containsKey("id")
						&& extras.containsKey("title")) {
					try {
						//ManagerNews.setNewsToHistory(this,
						//		Integer.parseInt(extras.getString("id")), true);
						Intent intentNotif = new Intent(this,
								NewsCollectionActivity.class);
						intentNotif.putExtra(NewsCollectionActivity.NEWS_ID_KEY,
								Integer.parseInt(extras.getString("id")));
						sendNotification(extras.getString("title"), intentNotif);
					} catch (Exception ignored) {
					}

				} else if (extras.containsKey("version")) {
					try {
						int newVersion = Integer.parseInt(extras
								.getString("version"));
						int currentVersion = Utils.getAppVersion(this);
						if (newVersion > currentVersion) {
							Intent intentNotif = new Intent(Intent.ACTION_VIEW);
							intentNotif.setData(Uri.parse(getResources()
									.getString(R.string.app_url)));
							sendNotification(getResources().getString(R.string.new_version_msg),
									intentNotif);
						}
					} catch (Exception e) {
					}
				}
				// sendNotification("Received: " + extras.toString());
				// Log.i(TAG, "Received: " + extras.toString());
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg, Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		String title = getResources().getString(R.string.app_name);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_notif).setContentTitle(title)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg).setSound(soundUri).setAutoCancel(true);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		NOTIFICATION_ID++;
	}

}
