package com.allNews.activity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.allNews.adapter.EventCategoryAdapter;
import com.allNews.data.Event;
import com.allNews.managers.ManagerEvents;
import com.allNews.managers.MyPreferenceManager;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import gregory.network.rss.R;

public class EventFilterActivity extends ActionBarActivity implements
		OnDateSetListener {
	private final int DIALOG_DATE_FROM = 1;
	private final int DIALOG_DATE_TO = 2;
	private int DIALOG_DATE = 0;
	private TextView filterFrom, filterTo, filterWeek, filterTomorrow,
			filterToday;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (MyPreferenceManager.getRotatePref(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		setContentView(R.layout.event_filter);
		setActionBar();
		initView();

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

		MenuItem m1 = menu.add("OK");
		m1.setIcon(R.drawable.ic_action_accept);
		m1.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
				intent.putExtra("filterFrom", filterFrom.getText());
				intent.putExtra("filterTo", filterTo.getText());
				setResult(RESULT_OK, intent);
				finish();
				return false;
			}
		});

		MenuItemCompat
				.setShowAsAction(m1, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	private void setActionBar() {
		ActionBar bar = getSupportActionBar();

		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setDisplayShowHomeEnabled(false);
			bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
					R.color.eventFilterBlue)));

			Spannable actionBarTitle = new SpannableString(getResources()
					.getString(R.string.tab_events));

			actionBarTitle
					.setSpan(
							new ForegroundColorSpan(getResources().getColor(
									R.color.white)), 0, actionBarTitle.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			bar.setTitle(actionBarTitle);
		}


	}

	private void initView() {
		setCategories();
		setCities();

		filterWeek = (TextView) findViewById(R.id.event_filter_week);
		filterFrom = (TextView) findViewById(R.id.event_filter_from);
		filterTo = (TextView) findViewById(R.id.event_filter_to);
		filterTomorrow = (TextView) findViewById(R.id.event_filter_tomorrow);
		filterToday = (TextView) findViewById(R.id.event_filter_today);

		filterFrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DIALOG_DATE_FROM);

			}

		});

		filterTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DIALOG_DATE_TO);

			}

		});

		filterTomorrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setFilterTomorrow();

			}

		});

		filterToday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setFilterToday();

			}

		});

		filterWeek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setFilterWeek();

			}

		});
	
	}

	private void setCities() {
		final AutoCompleteTextView filterCity = (AutoCompleteTextView) findViewById(R.id.event_filter_city);
		filterCity.setText(MyPreferenceManager.getEventCityFilter(this));
		filterCity.setSelection(filterCity.getText().length());
		filterCity.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				try {
					List<CharSequence> cities = ManagerEvents.getEventCity(
							EventFilterActivity.this, s.toString());
					ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
							EventFilterActivity.this,
							R.layout.item_drop_down_item, cities);

					filterCity.setAdapter(adapter);

					MyPreferenceManager.saveEventCityFilter(
							EventFilterActivity.this, s.toString());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		
		Button clearBtn = (Button)findViewById(R.id.event_filter_city_btn_clear);
		clearBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				filterCity.setText("");
				
			}
		});

	}

	private void setCategories() {
		ListView categoriesListView = (ListView) findViewById(R.id.event_filter_categories);

		try {
			final List<Event> events = ManagerEvents
					.getEventsWithUniqCategory(this);
			events.add(0, new Event());
			final EventCategoryAdapter adapter = new EventCategoryAdapter(this,
					events);
			categoriesListView.setAdapter(adapter);
			categoriesListView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

							if (position == 0) {
								if (MyPreferenceManager
										.isAllEventCategorySelected(EventFilterActivity.this))
									MyPreferenceManager
											.setAllEventCategorySelected(
													EventFilterActivity.this,
													false);
								else
									MyPreferenceManager
											.setAllEventCategorySelected(
													EventFilterActivity.this,
													true);

							} else
								MyPreferenceManager.eventCategoryFilterClick(
										EventFilterActivity.this,
										events.get(position).getCategory());

							adapter.notifyDataSetChanged();
						}

					});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void setFilterCustom() {
		filterToday.setBackgroundColor(getResources().getColor(R.color.white));
		filterToday.setTextColor(getResources().getColor(R.color.black));

		filterTomorrow.setBackgroundColor(getResources()
				.getColor(R.color.white));
		filterTomorrow.setTextColor(getResources().getColor(R.color.black));

		filterWeek.setBackgroundColor(getResources().getColor(R.color.white));
		filterWeek.setTextColor(getResources().getColor(R.color.black));

	}

	protected void setFilterToday() {
		filterToday.setBackgroundColor(getResources().getColor(R.color.grey));
		filterToday.setTextColor(getResources().getColor(
				R.color.eventFilterBlue));

		filterTomorrow.setBackgroundColor(getResources()
				.getColor(R.color.white));
		filterTomorrow.setTextColor(getResources().getColor(R.color.black));

		filterWeek.setBackgroundColor(getResources().getColor(R.color.white));
		filterWeek.setTextColor(getResources().getColor(R.color.black));

		Calendar calendarFrom = Calendar.getInstance();
		Calendar calendarTo = Calendar.getInstance();
		// calendarTo.add(Calendar.DATE, 7);
		setDateFilters(calendarFrom, calendarTo);
	}

	protected void setFilterTomorrow() {
		filterTomorrow
				.setBackgroundColor(getResources().getColor(R.color.grey));
		filterTomorrow.setTextColor(getResources().getColor(
				R.color.eventFilterBlue));

		filterToday.setBackgroundColor(getResources().getColor(R.color.white));
		filterToday.setTextColor(getResources().getColor(R.color.black));

		filterWeek.setBackgroundColor(getResources().getColor(R.color.white));
		filterWeek.setTextColor(getResources().getColor(R.color.black));

		Calendar calendarFrom = Calendar.getInstance();
		Calendar calendarTo = Calendar.getInstance();
		calendarFrom.add(Calendar.DATE, 1);
		calendarTo.add(Calendar.DATE, 1);
		setDateFilters(calendarFrom, calendarTo);
	}

	protected void setFilterWeek() {
		filterWeek.setBackgroundColor(getResources().getColor(R.color.grey));
		filterWeek.setTextColor(getResources()
				.getColor(R.color.eventFilterBlue));

		filterTomorrow.setBackgroundColor(getResources()
				.getColor(R.color.white));
		filterTomorrow.setTextColor(getResources().getColor(R.color.black));

		filterToday.setBackgroundColor(getResources().getColor(R.color.white));
		filterToday.setTextColor(getResources().getColor(R.color.black));

		Calendar calendarFrom = Calendar.getInstance();
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.add(Calendar.DATE, 7);
		setDateFilters(calendarFrom, calendarTo);
	}

	private void setDateFilters(Calendar calendarFrom, Calendar calendarTo) {

		filterFrom.setText(calendarFrom.get(Calendar.DAY_OF_MONTH) + "/"
				+ (calendarFrom.get(Calendar.MONTH) + 1) + "/"
				+ calendarFrom.get(Calendar.YEAR));
		filterTo.setText(calendarTo.get(Calendar.DAY_OF_MONTH) + "/"
				+ (calendarTo.get(Calendar.MONTH) + 1) + "/"
				+ calendarTo.get(Calendar.YEAR));
	}

	protected Dialog onCreateDialog(int id) {
		DIALOG_DATE = id;
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(this, this, year, month,
                day);

	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		String textFilter = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
		if (DIALOG_DATE == DIALOG_DATE_FROM) {

			if (!filterFrom.getText().equals(textFilter))
				setFilterCustom();
			filterFrom.setText(textFilter);
		} else if (DIALOG_DATE == DIALOG_DATE_TO) {
			if (!filterTo.getText().equals(textFilter))
				setFilterCustom();
			filterTo.setText(textFilter);
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

	// @Override
	// public void onBackPressed() {
	//
	// Intent intent = new Intent();
	// intent.putExtra("key", imporantSelect);
	// intent.putExtra("changeTheme", changeTheme);
	// setResult(RESULT_OK, intent);
	// finish();
	// }
}
