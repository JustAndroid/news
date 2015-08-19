package com.allNews.activity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allNews.managers.DialogManager;
import com.allNews.managers.ManagerSources;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.web.Statistic;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import gregory.network.rss.R;

public class HintsActivity extends ActionBarActivity {
	private LayoutInflater inflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (MyPreferenceManager.getRotatePref(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		setContentView(R.layout.hints_layout);
		initActionBar();
		String[] hints = getResources().getStringArray(R.array.hints);
		inflater = LayoutInflater.from(this);
		LinearLayout main = (LinearLayout) findViewById(R.id.hintLayoutMain);
		for (int i = 0; i < hints.length; i++) {
			View hintView = getHintView(i, hints[i]);
			main.addView(hintView);

		}
	}

	private View getHintView(int i, String text) {
		View hintView = inflater.inflate(R.layout.hint_item, null);
		TextView hintTextView = (TextView) hintView.findViewById(R.id.hintText);
		hintTextView.setText(Html.fromHtml(text));
	 	hintTextView.setMovementMethod(LinkMovementMethod.getInstance());
		if (i == 0) {
		    setShareBtn(hintView);
		} 
		else if (i == 2) {
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(this);
			String updateTime = ""+(Integer.parseInt(sp.getString(Preferences.PREF_UPDATE,
					"1800"))/60);

			hintTextView.setText(getResources().getString(R.string.hint_3,
					updateTime));
		}
		else if (i == 3) {
			setFeedbackBtn(hintView);
		} else if (i == 5) {
			String sourcesCount = ManagerSources
					.getAllSourcesCount(HintsActivity.this);
			String checkedSourcesCount = ManagerSources
					.getCheckedSourcesCount(HintsActivity.this);
			hintTextView.setText(getResources().getString(R.string.hint_6,
					sourcesCount, checkedSourcesCount));
		} else if (i == 7) {
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(this);
			String maxHoursToSaveNews = sp.getString(Preferences.PREF_SAVE,
					"12");

			hintTextView.setText(getResources().getString(R.string.hint_8,
					maxHoursToSaveNews));

		}
		return hintView;
	}

	private void setShareBtn(View hintView) {
		ImageButton hintShareApp = (ImageButton) hintView
				.findViewById(R.id.hintShare);
		hintShareApp.setVisibility(View.VISIBLE);
		hintShareApp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogManager.openDialogShareUS(HintsActivity.this,
						Statistic.LABEL_SHARE_FROM_HINTS);

			}
		});

	}

	private void setFeedbackBtn(View hintView) {
		ImageButton hintShareApp = (ImageButton) hintView
				.findViewById(R.id.hintShare);
		hintShareApp.setVisibility(View.VISIBLE);
		hintShareApp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogManager.openDialogFeedBack(HintsActivity.this);

			}
		});

	}

	private void initActionBar() {
		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setDisplayShowHomeEnabled(false);
			bar.setTitle(getResources().getString(R.string.hints_title));
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
