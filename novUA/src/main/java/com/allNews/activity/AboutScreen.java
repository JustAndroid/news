package com.allNews.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.allNews.managers.DialogManager;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.web.Statistic;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;


import gregory.network.rss.R;

public class AboutScreen extends ActionBarActivity {
	final String LOG_TAG = "LogsAboutScreen";

	private int mClickCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (!getResources().getBoolean(R.bool.need_event))
			return;
		if (getResources().getBoolean(R.bool.sport_news)){

		}
		if (MyPreferenceManager.getRotatePref(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		setContentView(R.layout.about);
		ActionBar bar = getSupportActionBar();
		// bar.setDisplayShowTitleEnabled(false);
		bar.setDisplayHomeAsUpEnabled(true); 
		bar.setDisplayShowHomeEnabled(false);
		bar.setTitle(getResources().getString(R.string.about_preferences));

		initDevMode();

		//TextView txtApp2Title = (TextView) findViewById(R.id.txtApp2Title);
		//String app2_title = getResources().getString(
		//		R.string.about_other_app2_title);
		/*String app2_description = getResources().getString(
				R.string.about_other_app2_description);
		txtApp2Title.setText(Html.fromHtml("<big><b>" + app2_title
				+ "</b></big> " + app2_description));

		TextView txtApp3Title = (TextView) findViewById(R.id.txtApp3Title);
		String app3_title = getResources().getString(
				R.string.about_other_app3_title);
		String app3_description = getResources().getString(
				R.string.about_other_app3_description);
		txtApp3Title.setText(Html.fromHtml("<big><b>" + app3_title
				+ "</b></big> " + app3_description));

		TextView txtApp4Title = (TextView) findViewById(R.id.txtApp4Title);
		String app4_title = getResources().getString(
				R.string.about_other_app4_title);
		String app4_description = getResources().getString(
				R.string.about_other_app4_description);
		txtApp4Title.setText(Html.fromHtml("<big><b>" + app4_title
				+ "</b></big> " + app4_description));

		TextView txtApp5Title = (TextView) findViewById(R.id.txtApp5Title);
		String app5_title = getResources().getString(
				R.string.about_other_app5_title);
		String app5_description = getResources().getString(
				R.string.about_other_app5_description);
		txtApp5Title.setText(Html.fromHtml("<big><b>" + app5_title
				+ "</b></big> " + app5_description));

		setShareBtn(R.id.btnShareApp1,
				getResources().getString(R.string.about_other_app1),
				getResources().getString(R.string.app_android_1) + "\n\n"
						+ getResources().getString(R.string.app_ios_1));
		setShareBtn(R.id.btnShareApp2, app2_title,
				getResources().getString(R.string.app_android_2) + "\n\n"
						+ getResources().getString(R.string.app_ios_2));
		setShareBtn(R.id.btnShareApp3, app2_title,
				getResources().getString(R.string.app_android_3) + "\n\n"
						+ getResources().getString(R.string.app_ios_3));
		setShareBtn(R.id.btnShareApp4, app2_title,
				getResources().getString(R.string.app_android_4));
		setShareBtn(R.id.btnShareApp5, app2_title,
				getResources().getString(R.string.app_android_5) + "\n\n"
						+ getResources().getString(R.string.app_ios_5));

		setShareBtn(R.id.btnShare,
				getResources().getString(R.string.about_other_app6),
				getResources().getString(R.string.app_android_6));

		setShareBtn(R.id.btnShareApp7,
				getResources().getString(R.string.about_other_app7),
				getResources().getString(R.string.app_android_7) + "\n\n"
						+ getResources().getString(R.string.app_ios_7));*/
	}

	private void initDevMode() {
		TextView txt = (TextView) findViewById(R.id.hintText);
		txt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mClickCount++;
				if (mClickCount < 5)
					return;
				DialogManager.showAdminDialog(AboutScreen.this);

			}
		});

	}

	private void setShareBtn(int btnshareapp, final String appName,
			final String urls) {
		ImageButton ib = (ImageButton) findViewById(btnshareapp);
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				share(appName, urls);

			}
		});

	}

	protected void share(String appName, String urls) {
		Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		sendIntent.setType("text/plain");
		sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, appName);
		sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, urls);
		try {
			Statistic.sendStatistic(AboutScreen.this, Statistic.CATEGORY_CLICK,
					Statistic.ACTION_CLICK_BTN_SHARE, appName, 0L);

			startActivity(Intent.createChooser(sendIntent, getResources()
					.getString(R.string.app_name) + ":"));
		} catch (android.content.ActivityNotFoundException ignored) {
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			this.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this,
				getResources().getString(R.string.fluri_id));
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
		EasyTracker.getInstance(this).activityStop(this);
	}

}