package com.allNews.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.allNews.managers.MyPreferenceManager;
import com.allNews.web.Statistic;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import gregory.network.rss.R;

public class OtherAppScreen extends ActionBarActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (MyPreferenceManager.getRotatePref(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		setContentView(R.layout.other_app_layout);
		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setDisplayShowHomeEnabled(false);
			bar.setTitle(getResources().getString(R.string.all_app));
		}


		View v1 = (View) findViewById(R.id.download_app_layout1);
		View v2 = (View) findViewById(R.id.download_app_layout2);
		View v3 = (View) findViewById(R.id.download_app_layout3);
		View v4 = (View) findViewById(R.id.download_app_layout4);

		setApp(v1, getResources().getDrawable(R.drawable.ic_2event_icon),
				getString(R.string.about_other_app3_title),
				getString(R.string.description_app_2event),
				getString(R.string.app_android_3));

		setApp(v2, getResources().getDrawable(R.drawable.ic_whoisaround_icon),
				getString(R.string.about_other_app4_title),
				getString(R.string.description_app_whoisaround),
				getString(R.string.app_android_4));

		String pakage = getPackageName();
		if (pakage.contains(".rss")) {
			setAppNewsRu(v3);
			setAppNewsStartUp(v4);
		} else if (pakage.contains(".it")) {
			setAppNewsRu(v3);
			setAppNewsUa(v4);
		} else if (pakage.contains(".ru")) {
			setAppNewsUa(v3);
			setAppNewsStartUp(v4);
		}
	}

	private void setAppNewsUa(View v) {
		setApp(v, getResources().getDrawable(R.drawable.ic_news_ua),
				getString(R.string.about_other_app1), "",
				getString(R.string.app_android_1));
	}

	private void setAppNewsRu(View v) {
		setApp(v, getResources().getDrawable(R.drawable.ic_news_ru),
				getString(R.string.about_other_app6), "",
				getString(R.string.app_android_6));
	}

	private void setAppNewsStartUp(View v) {
		setApp(v, getResources().getDrawable(R.drawable.ic_startup),
				getString(R.string.about_other_app7), "",
				getString(R.string.app_android_7));
	}

	private void setApp(View v, Drawable drawable, final String title,
			String description, final String link) {

		ImageView image = (ImageView) v.findViewById(R.id.imgDownloadApp);
		image.setImageDrawable(drawable);

		TextView txtTitle = (TextView) v.findViewById(R.id.txtTitleDownloadApp);
		txtTitle.setText(title);

		TextView txtDescription = (TextView) v
				.findViewById(R.id.txtDownloadApp);
		txtDescription.setText(description);

		Button btnDownloadApp = (Button) v.findViewById(R.id.btnDownloadApp);
		btnDownloadApp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Statistic.sendStatistic(OtherAppScreen.this,
						Statistic.CATEGORY_CLICK,
						Statistic.ACTION_CLICK_BTN_OUR_APP, title, 0L);

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(link));
				startActivity(intent);

			}
		});
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