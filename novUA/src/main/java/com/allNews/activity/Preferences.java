package com.allNews.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.allNews.data.Categories;
import com.allNews.data.Source;
import com.allNews.db.DatabaseHelper;
import com.allNews.managers.DialogManager;
import com.allNews.managers.ManagerCategories;
import com.allNews.managers.ManagerSources;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.view.PrefSelectAll;
import com.allNews.web.Statistic;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import gregory.network.rss.R;

public class Preferences extends PreferenceActivity {


	public static final int PASSWORD_FOR_PROMO =-1124118858 ;
	public static final String PREF_PROMO = "PromoCode";
	private boolean needRefresh = false;
	private boolean needRestart = false;
	private boolean needUpdate = false;
	private boolean needChangetab = false;
	private boolean firstStart = false;
	private boolean needChangeAutoUpdateTime = false;
    private boolean weatherOf = false;
	public static final String LANGUAGE_EN = "en";
	public static final String LANGUAGE_UA = "uk";
	public static final String LANGUAGE_RU = "ru";
	public static final String PREF_THEME = "theme";
	public static final String PREF_PAGE = "prefPage";
	public static final String PREF_LANGUAGE = "prefLang";
	public static final String PREF_FEED = "prefFeedNews";
	public static final String PREF_RUN = "firstTimeRun1";
	public static final String PREF_UPDATE = "RefreshPref";
	public static final String PREF_MESSAGE_TIME = "prefMessageTime";
	public static final String PREF_SOURCES = "NewsPrefScreen";
	public static final String PREF_SAVE = "SavePref";
	public static final String PREF_SIZE = "prefSize";
	public static final String PREF_VIEW_MODE = "prefViewMode";
	public static final String PREF_SHOW_US = "prefShowUs";
	public static final String PREF_ADMIN = "prefAdmin";
	public static final String PREF_FOR_READ_ALL_NEWS = "prefPass";
	public static final String PREF_CATEGORY = "prefCategory";
	public static final String PREF_ORIENTATION = "autoRotatePref";
	public static final String PREF_BEST_NEWAPP = "prefBestNewApp";
	public static final int PASSWORD_FOR_PUSH = -906277200;
	public static final String PASSWORD_FOR_READ_ALL_NEWS = "q";
	public static final String LATITUDE = "latitude";
    public static final String lONGITUDE = "longitude";
    public static final String PREF_WEATHER = "prefWeather";
    public static final String CITY_ID = "cityid";
    public static final String COUNTRY_CODE = "country_code";
	public  static final String PREF_NOTIFICATION ="prefNotification";


	private String sourcesToUpdate = "";

	// private static final String KEY_PREF_SOURCE_ALL = "sourceAllPref";
	// private static final String KEY_PREF_CATEGORY_ALL = "categoryAllPref";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (MyPreferenceManager.getRotatePref(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		Toast.makeText(getBaseContext(), getString(R.string.pref_msg),
				Toast.LENGTH_SHORT).show();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
				/*&& getActionBar() != null*/) {

			ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                actionBar.setIcon(R.drawable.ic_launcher);
                actionBar.setDisplayShowTitleEnabled(true);

                actionBar.setTitle(R.string.settings);

                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(false);
            }

		} else
			setTitle(getResources().getString(R.string.settings));

		if (getIntent().getExtras() != null)
			if (getIntent().getExtras().getBoolean("sources")) {
				addPreferencesFromResource(R.xml.preferences_first);
				try {
					setSources();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sp.getBoolean(Preferences.PREF_RUN, true)) {

			addPreferencesFromResource(R.xml.preferences_first);

			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(Preferences.PREF_RUN, false);
			editor.apply();

			needUpdate = true;
			firstStart = true;
			try {
				setSources();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			addPreferencesFromResource(R.xml.preferences);
			try {
				setSources();
				if (getResources().getBoolean(R.bool.need_event))
				setCategory();

				setPrefSummary();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CheckBoxPreference orientationPref = (CheckBoxPreference) findPreference(PREF_ORIENTATION);
			orientationPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							needRestart = true;

							return true;
						}
					});

			Preference customPref = findPreference("customPref");
			customPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							needRefresh = true;
							DialogManager.showDeleteDbDialog(Preferences.this);

							return true;
						}
					});

			Preference prefAbout = findPreference("prefAbout");

			prefAbout
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							Intent settingsActivity = new Intent(
									Preferences.this, AboutScreen.class);
							startActivity(settingsActivity);
							return true;
						}
					});
            CheckBoxPreference prefWeather = (CheckBoxPreference) findPreference("prefWeather");
            prefWeather.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					needRestart = true;
					return false;
				}
			});
			CheckBoxPreference prefNotification = (CheckBoxPreference) findPreference(PREF_NOTIFICATION);
			prefNotification.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
                    needRestart = true;
					return false;
				}
			});





			Preference prefShare = findPreference("sharePref");

			prefShare
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							DialogManager.openDialogShareUS(Preferences.this,
									Statistic.LABEL_SHARE_FROM_SETTINGS);

							return true;
						}
					});

			Preference preffeedback = findPreference("feedbackPref");
			preffeedback
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							DialogManager.openDialogFeedBack(Preferences.this);

							return true;
						}
					});

			try {
				Preference prefPass = findPreference("prefPass");
				prefPass
						.setOnPreferenceClickListener(new OnPreferenceClickListener() {

							@Override
							public boolean onPreferenceClick(Preference preference) {
								DialogManager.showPasswordDialog(Preferences.this);

								return true;
							}
						});

//				Preference prefApp = findPreference("prefApps");
//				prefApp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//
//					@Override
//					public boolean onPreferenceClick(Preference preference) {
//						Intent settingsActivity = new Intent(Preferences.this,
//								OtherAppScreen.class);
//						startActivity(settingsActivity);
//						return true;
//					}
//				});
			} catch (Exception ignored) {
			}

			Preference prefApp = findPreference("prefApps");
			prefApp
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							Intent settingsActivity = new Intent(Preferences.this,
									OtherAppScreen.class);
							startActivity(settingsActivity);
							return true;
						}
					});
		}
	}

	private void setCategory() throws Exception {
		PreferenceScreen targetCategory = (PreferenceScreen) findPreference(PREF_CATEGORY);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		addCheckedAllCategoriesPref(targetCategory);

		Dao<Categories, Integer> dao = OpenHelperManager.getHelper(
				Preferences.this, DatabaseHelper.class).getCategoryDao();
		List<Categories> categoriesList = dao.queryForAll();
		for (int i = 0; i < categoriesList.size(); i++) {
			String key = categoriesList.get(i).getEn();
			SharedPreferences.Editor editor = sp.edit();
			CheckBoxPreference categoryPref = new CheckBoxPreference(this);
			categoryPref.setTitle(categoriesList.get(i).getCategoryName(this));
			categoryPref.setKey(key);
			  
			if (categoriesList.get(i).getIsActive() == 1) {
				categoryPref.setChecked(true);
				editor.putBoolean(key, true);
			} else {
				categoryPref.setChecked(false);
				editor.putBoolean(key, false);
			}

			editor.commit();

			targetCategory.addPreference(categoryPref);
		}

	}

	private void addCheckedAllCategoriesPref(PreferenceScreen targetCategory)
			throws NotFoundException, SQLException {
		// Preference categoryAllPref = new Preference(this);
		PrefSelectAll categoryAllPref = new PrefSelectAll(this,
				PrefSelectAll.TYPE_CATEGORY);
		categoryAllPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						selectAllCategory();
						return false;
					}
				});
		// categoryAllPref.setKey(KEY_PREF_CATEGORY_ALL);
		targetCategory.addPreference(categoryAllPref);

	}

	private void setSources() throws SQLException {
		PreferenceScreen targetCategory = (PreferenceScreen) findPreference(PREF_SOURCES);
  
		addCheckedAllPref(targetCategory);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		Dao<Source, Integer> dao = OpenHelperManager.getHelper(
				Preferences.this, DatabaseHelper.class).getSourceDao();
		List<Source> sourceList = dao.queryForAll();
		for (int i = 0; i < sourceList.size(); i++) {
			String key = "" + sourceList.get(i).getSourceID();
			SharedPreferences.Editor editor = sp.edit();
			CheckBoxPreference sourcePref = new CheckBoxPreference(this);

			sourcePref.setKey(key);
			sourcePref.setTitle(sourceList.get(i).getName());

			if (sourceList.get(i).getIsActive() == 1) {
				sourcePref.setChecked(true);
				editor.putBoolean(key, true);
			} else {
				sourcePref.setChecked(false);
				editor.putBoolean(key, false);
				if (key.equals(Preferences.this.getResources().getString(R.string.ad_source_id))){
					Statistic.sendStatistic(Preferences.this,Statistic.CATEGORY_PRESS_RELEASE_B2B, "TURN OFF B2B", " ", 0l);
				}

			}

			editor.apply();

			targetCategory.addPreference(sourcePref);



		}
	}

	private void addCheckedAllPref(PreferenceScreen targetCategory) {
		// Preference sourceAllPref = new Preference(this);
		PrefSelectAll sourceAllPref = new PrefSelectAll(this,
				PrefSelectAll.TYPE_SOURCE);

		sourceAllPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						selectAllSources();
						return false;
					}
				});
		// sourceAllPref.setKey(KEY_PREF_SOURCE_ALL);
		targetCategory.addPreference(sourceAllPref);

	}

	private void setPrefSummary() {
		ListPreference prefTime = (ListPreference) findPreference(PREF_SAVE);
		if (prefTime == null)
			return;
		prefTime.setSummary(prefTime.getEntry());

		ListPreference prefRefresh = (ListPreference) findPreference(PREF_UPDATE);
		prefRefresh.setSummary(prefRefresh.getEntry());

		ListPreference prefLang = (ListPreference) findPreference(PREF_LANGUAGE);
		prefLang.setSummary(prefLang.getEntry());

		ListPreference prefSize = (ListPreference) findPreference(PREF_SIZE);
		prefSize.setSummary(prefSize.getEntry());

		PreferenceScreen prefSource = (PreferenceScreen) findPreference(PREF_SOURCES);
		prefSource.setSummary(getSummaryForSources(prefSource));

		((BaseAdapter) prefSource.getRootAdapter()).notifyDataSetChanged();
		if (getResources().getBoolean(R.bool.need_event)) {
			PreferenceScreen prefCategory = (PreferenceScreen) findPreference(PREF_CATEGORY);
			prefCategory.setSummary(getSummaryForCategories(prefCategory));
			((BaseAdapter) prefCategory.getRootAdapter())
					.notifyDataSetChanged();
		}
	}

	private CharSequence getSummaryForCategories(PreferenceScreen prefCategory) {
		int checkedCount = 0;
		for (int i = 1; i < prefCategory.getPreferenceCount(); i++) {
			CheckBoxPreference sourcePref = (CheckBoxPreference) prefCategory
					.getPreference(i);

			if (sourcePref.isChecked())
				checkedCount++;
		}
		return getResources()
				.getString(R.string.pref_category, "" + checkedCount,
						"" + (prefCategory.getPreferenceCount() - 1));
	}

	private CharSequence getSummaryForSources(PreferenceScreen targetCategory) {
		int checkedCount = 0;
		for (int i = 1; i < targetCategory.getPreferenceCount(); i++) {
			CheckBoxPreference sourcePref = (CheckBoxPreference) targetCategory
					.getPreference(i);
			if (sourcePref.isChecked())
				checkedCount++;
		}
		return getResources().getString(R.string.pref_source,
				"" + checkedCount,
				"" + (targetCategory.getPreferenceCount() - 1));

	}

	SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(final SharedPreferences prefs,
				final String key) {

			// Log.e("key", key);
			setPrefSummary();
			if (key.equals(PREF_SIZE))
				needRefresh = true;
			else if (key.equals(PREF_LANGUAGE)) {
				needRestart = true;

				setLanguage();
			} else if (key.equals(PREF_UPDATE)) {
				needChangeAutoUpdateTime = true;
			}


			else if (ManagerSources.isSourceExist(Preferences.this, key) != null) {
				updateSourceInThread(prefs, key);
			}

			else {
				if (ManagerCategories.isCategoryExist(Preferences.this, key) != null) {
					updateCategoryInThread(prefs, key);
				}
			}
		}

	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return false;
	}

	protected void selectAllSources() {
		try {
			boolean checked = true;
			if (ManagerSources.isAllSourcesChecked(this) == 1) {
				checked = false;
				sourcesToUpdate = "";
			}

			List<Source> sources = ManagerSources.getAllSources(this, !checked);

			for (int i = 0; i < sources.size(); i++) {
				Preference pref = findPreference(""
						+ sources.get(i).getSourceID());
				if (pref instanceof CheckBoxPreference) {
					CheckBoxPreference check = (CheckBoxPreference) pref;
					check.setChecked(checked);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void selectAllCategory() {
		try {
			boolean checked = true;
			if (ManagerCategories.isAllCategoriesChecked(this) == 1) {
				checked = false;

			}

			List<Categories> categories = ManagerCategories.getAllCategories(
					this, !checked);

			for (int i = 0; i < categories.size(); i++) {
				Preference pref = findPreference(categories.get(i).getEn());
				if (pref instanceof CheckBoxPreference) {
					CheckBoxPreference check = (CheckBoxPreference) pref;
					check.setChecked(checked);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void updateSourceInThread(final SharedPreferences prefs,
			final String key) {
		final Handler handler = new Handler();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					updateSource(prefs, key);
					handler.post(new Runnable() {

						@Override
						public void run() {
							PreferenceScreen prefSource = (PreferenceScreen) findPreference(PREF_SOURCES);

							((BaseAdapter) prefSource.getRootAdapter())
									.notifyDataSetChanged();
						}
					});
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	protected void updateSource(SharedPreferences prefs, String key)
			throws SQLException {
		Source source = ManagerSources.isSourceExist(this, key);
		if (source != null) {
			source.setIsActive(prefs.getBoolean(key, true) == true ? 1 : 0);
			ManagerSources.updateSource(this, source);
			needUpdate = true;
			if (prefs.getBoolean(key, true) && !firstStart)
				sourcesToUpdate = sourcesToUpdate + key + ",";
		}
	}

	protected void updateCategoryInThread(final SharedPreferences prefs,
			final String key) {
		final Handler handler = new Handler();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					updateCategory(prefs, key);
					handler.post(new Runnable() {

						@Override
						public void run() {
							PreferenceScreen prefCategory = (PreferenceScreen) findPreference(PREF_CATEGORY);
							((BaseAdapter) prefCategory.getRootAdapter())
									.notifyDataSetChanged();

						}

					});
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	protected void updateCategory(SharedPreferences prefs, String key)
			throws SQLException {
		Categories category = ManagerCategories.isCategoryExist(this, key);
		if (category != null) {
			category.setIsActive(prefs.getBoolean(key, true) == true ? 1 : 0);
			ManagerCategories.updateCategory(this, category);
			needChangetab = true;

		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(listener);
	}

	public void setLanguage() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		String lang = sp.getString(Preferences.PREF_LANGUAGE, LANGUAGE_UA);

		Locale myLocale = new Locale(lang);

		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

	}

	@Override
	public void onBackPressed() {

		if (ManagerSources.isAllSourcesChecked(this) == 0) {
			Toast.makeText(getBaseContext(),
					getString(R.string.no_sources_msg), Toast.LENGTH_LONG)
					.show();
			return;
		}

		Intent intent = new Intent();
		intent.putExtra("key", needRefresh);
		intent.putExtra("key2", needRestart);
		intent.putExtra("key3", needUpdate);

		intent.putExtra("key4", needChangeAutoUpdateTime);
		intent.putExtra("key5", sourcesToUpdate);
		intent.putExtra("key6", needChangetab);

		setResult(RESULT_OK, intent);
		finish();
	}
}