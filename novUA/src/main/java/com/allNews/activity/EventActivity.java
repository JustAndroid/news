package com.allNews.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.allNews.data.Event;
import com.allNews.managers.EWLoader;
import com.allNews.managers.ManagerEvents;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;
import com.allNews.web.Statistic;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import gregory.network.rss.R;

public class EventActivity extends ActionBarActivity {
	private String eventName = "";
	private String eventUrl = "";
	private int curTheme;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (MyPreferenceManager.getRotatePref(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		setContentView(R.layout.event_item);
	 
		curTheme =  MyPreferenceManager.getCurrentTheme(this);

		if (getIntent().getExtras() != null) {
			Long eventID = getIntent().getExtras().getLong("event_id");
			try {
				Event event = ManagerEvents.getEventById(this, eventID);
				if (event != null){
					initWidget(event);
				setActionBar(event);
				}
			} catch (SQLException e) {
				onBackPressed();
			}

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {

		MenuItem m = menu.add("Share");
		m.setIcon(curTheme == AllNewsActivity.THEME_WHITE ? R.drawable.ic_share
				: R.drawable.ic_share_dark);
		m.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

				share();
				return false;
			}
		});

		MenuItemCompat
				.setShowAsAction(m, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	protected void share() {
		String text =getResources().getString(
				R.string.share_event_msg) ;
		
		Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		sendIntent.setType("text/plain");
		sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, text);
		sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, eventName + "\n\n"
				+ eventUrl);
		try {
			Statistic.sendStatistic(EventActivity.this,
					Statistic.CATEGORY_CLICK, Statistic.ACTION_CLICK_BTN_SHARE,
					"event", 0L);

			startActivity(Intent.createChooser(sendIntent,  getResources()
					.getString(R.string.app_name) + ":"));
		} catch (android.content.ActivityNotFoundException ignored) {
		}

	}

	private void setActionBar(Event event) {
		final ActionBar bar = getSupportActionBar();

		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setDisplayShowHomeEnabled(false);


			// bar.setTitle(event.getName(EventActivity.this));
			if (curTheme == AllNewsActivity.THEME_DARK) {

				bar.setBackgroundDrawable(new ColorDrawable(getResources()
						.getColor(R.color.bgActionBarNight)));

			} else {

				bar.setBackgroundDrawable(new ColorDrawable(getResources()
						.getColor(R.color.bgActionBarDay)));

			}

			Spannable actionBarTitle = new SpannableString(
					event.getName(EventActivity.this));

			actionBarTitle
					.setSpan(
							new ForegroundColorSpan(
									curTheme == AllNewsActivity.THEME_WHITE ? getResources()
											.getColor(R.color.black)
											: getResources().getColor(
											R.color.txtGrey)), 0,
							actionBarTitle.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			bar.setTitle(actionBarTitle);
		}
	}

	private void initWidget(final Event event) {
		eventName = event.getName(this);
		eventUrl = event.getPageUrl();
		SimpleDateFormat formatDate = new SimpleDateFormat(
				"dd MMM, yyy \nHH:mm");

		TextView eventAbout = (TextView) findViewById(R.id.eventAbout);
		TextView eventAboutTitle = (TextView) findViewById(R.id.eventAboutTitle);
		TextView eventAddress = (TextView) findViewById(R.id.eventAddress);
		final TextView eventCalendar = (TextView) findViewById(R.id.eventCalendar);
		final TextView eventDirections = (TextView) findViewById(R.id.eventDirections);
		TextView eventPrice = (TextView) findViewById(R.id.eventPrice);
		TextView eventTime = (TextView) findViewById(R.id.eventTime);
		ImageView eventImage = (ImageView) findViewById(R.id.eventImg);
		Button eventBuy = (Button) findViewById(R.id.eventBuyBtn);
		LinearLayout contentLayout = (LinearLayout) findViewById(R.id.eventContentLayout);
		LinearLayout priceLayout = (LinearLayout) findViewById(R.id.eventPriceLayout);

		if (curTheme == AllNewsActivity.THEME_DARK) {

			contentLayout.setBackgroundColor(getResources().getColor(
					R.color.bgNight));
			priceLayout.setBackgroundColor(getResources().getColor(
					R.color.bgActionBarNight));
			eventPrice.setTextColor(getResources().getColor(R.color.white));
			eventAbout.setTextColor(getResources().getColor(R.color.white));
			eventAddress.setTextColor(getResources().getColor(R.color.white));
			eventCalendar.setTextColor(getResources().getColor(R.color.white));
			eventDirections
					.setTextColor(getResources().getColor(R.color.white));
			eventTime.setTextColor(getResources().getColor(R.color.white));
			eventAboutTitle
					.setTextColor(getResources().getColor(R.color.white));

			eventTime.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_big_calendar_dark, 0, 0, 0);
			eventAddress.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_big_location_dark, 0, 0, 0);
			eventDirections.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.ic_direction_dark, 0, 0);
			eventCalendar.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.ic_add_to_calendar_dark, 0, 0);
		}

		if (event.getLogoSmallUrl() != null
				&& Utils.isUrlValid(event.getLogoBigUrl())) {
			EWLoader.loadImg(this, event.getLogoBigUrl(), eventImage);
		}
		 
		eventAddress.setText(event.getAddress());

		if (event.isFree())
			eventPrice.setText("" + event.getPrice());
		else if (event.getPrice() > 0 && event.getCurrency() != null)
			eventPrice.setText(event.getPrice() + " " + event.getCurrency());

		if (event.getDescription() != null)
			eventAbout.setText(Html.fromHtml(event.getDescription()));
		eventTime.setText(formatDate.format(new Date(
				event.getStartDate() * 1000)));

		if (event.getPageUrl() != null && Utils.isUrlValid(event.getPageUrl())) {
			if (event.isFree() || event.getPrice() == 0)
				eventBuy.setText(getResources().getString(R.string.to_web));
			
			eventBuy.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Statistic.sendStatistic(EventActivity.this,
							Statistic.CATEGORY_EVENTS,
							Statistic.ACTION_CLICK_EVENT_BUY,
							event.getName(EventActivity.this), 0L);
					try {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW,
								Uri.parse(event.getPageUrl()));
						startActivity(browserIntent);
					} catch (Exception ignored) {
					}

				}
			});
		} else
			eventBuy.setVisibility(View.INVISIBLE);

		eventDirections.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri geoLocation = Uri.parse("geo:0,0?q="
							+ event.getAddress() + "");
					if (event.getMapLat() > 0 && event.getMapLng() > 0)
						geoLocation = Uri.parse("geo:0,0?q="
								+ event.getMapLat() + "," + event.getMapLng()
								+ "(" + event.getName(EventActivity.this) + ")");

					intent.setData(geoLocation);
					if (intent.resolveActivity(getPackageManager()) != null) {
						startActivity(intent);
					}
				} catch (Exception ignored) {
				}
			}
		});

		eventCalendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_INSERT)
						.setType("vnd.android.cursor.item/event")
						.putExtra("title", event.getName(EventActivity.this))
						.putExtra("eventLocation", event.getAddress())
						.putExtra("beginTime", event.getStartDate() * 1000);

				if (intent.resolveActivity(getPackageManager()) != null) {
					startActivity(intent);
				}

			}
		});

		eventCalendar.post(new Runnable() {
			public void run() {
				int w = eventCalendar.getMeasuredWidth();

				eventDirections.setLayoutParams(new LinearLayout.LayoutParams(
						w, LayoutParams.WRAP_CONTENT));
			}
		});

	}

}
