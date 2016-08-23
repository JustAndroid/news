package com.allNews.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.allNews.activity.AboutScreen;
import com.allNews.activity.AllNewsActivity;
import com.allNews.activity.HintsActivity;
import com.allNews.activity.Preferences;
import com.allNews.data.NewApp.NewApp;
import com.allNews.data.News;
import com.allNews.db.DatabaseHelper;
import com.allNews.web.Statistic;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gregory.network.rss.R;

public class Utils {
	public static final long MINUTE = 60 * 1000;
    private static final int STATUS_CODE_OK = 200;

    public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {

			return 0;
		}
	}

	public static void isOnline(final Context mContext,
			final Handler internetCheckHandler) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				if (checkInternetConnection(mContext, false))
					internetCheckHandler.sendEmptyMessage(1);
				else
					internetCheckHandler.sendEmptyMessage(0);
			}
		}).start();

	}

	public static void isOnlineAfterError(final Context mContext) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				checkInternetConnection(mContext, true);

			}
		}).start();

	}

    public static boolean checkInternetConnection(Context mContext,
                                                  boolean isAfterError) {
		ConnectivityManager ConMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (ConMgr != null) {
			NetworkInfo netInfo = ConMgr.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()
					&& ConMgr.getActiveNetworkInfo() != null
					&& ConMgr.getActiveNetworkInfo().isAvailable()
					&& ConMgr.getActiveNetworkInfo().isConnected()) {

				HttpGet getReq = new HttpGet("http://google.com");
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = null;
				try {
					response = httpClient.execute(getReq);
				} catch (Exception e) {
					return false;

				}
				if (response != null
						&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					if (mContext.getResources().getBoolean(R.bool.sport_news))
						return true;
					//	checkIP(mContext);


				}
			}

		}
		return true;
	}

	private static void checkIP(Context mContext) {
		try {
			InetAddress address = InetAddress.getByName("www.all-news.com.ua");
			if (!address.getHostAddress().equals("31.28.161.242"))
				Statistic.sendErrorStatistic(mContext,
						Statistic.CATEGORY_ERROR, Statistic.ACTION_ERROR_INET,
						Statistic.LABEL_ERROR_DNS_ALLNEWS_SERVER, 0L);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static Boolean isOnline(Context mContext) {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getDate(long time) {

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(time));
	}

	public static String getDate(String pubDate) {

		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK)
					.parse(pubDate);
			return new SimpleDateFormat("yyyy-MM-dd").format(date);
		} catch (ParseException e) {
			return "";
		}

	}

	public static String getCurrentDate() {

		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System
				.currentTimeMillis()));
	}

	public static String getCurrentDateAndTime() {

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
				System.currentTimeMillis()));
	}

	public static String getTimeHHMM(long time) {

		return new SimpleDateFormat("HH:mm").format(new Date(time));
	}

	public static long getTime(String pubDate) {
		long time = 0;
		String stringDateFormat = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(stringDateFormat,
				Locale.UK);
		Date date = null;
		try {
			date = format.parse(pubDate);
		} catch (ParseException e) {
			return time;
		}

		if (date != null)
			time = date.getTime();
		else
			time = new Date().getTime();
		return time;
	}

	public static String getCurrentTime() {
		return new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").format(new Date(
				System.currentTimeMillis()));

	}

	public static long getDaysAgo(long time) {

		long millis = System.currentTimeMillis() - time;
		return TimeUnit.MILLISECONDS.toDays(millis);

	}

	public static long getHourAgo(long time) {

		long millis = System.currentTimeMillis() - time
				- TimeUnit.DAYS.toMillis(getDaysAgo(time));

		return TimeUnit.MILLISECONDS.toHours(millis);

	}

	public static String getMinAgo(long time) {

		long millis = System.currentTimeMillis() - time;
        return String.format(
				"%02d ",// TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
						.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
						.toMinutes(millis)));

	}

	public static boolean isSomethingValid(CharSequence something,
			String patternStr) {
		try {
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(something);

			if (!matcher.matches())
				return false;

		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	public static boolean isUrlValid(CharSequence something) {

		try {
			Pattern pattern = Patterns.WEB_URL;
			Matcher matcher = pattern.matcher(something);

			if (!matcher.matches())
				return false;

		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	public static List<String> getAllMatches(String text, String regex,
			String pattern) {
		List<String> matches = new ArrayList<>();
		Matcher m = Pattern.compile("(?=(" + regex + "))").matcher(text);
		while (m.find()) {
			if (m.group(1).contains(pattern))
				matches.add(m.group(1));
		}
		return matches;
	}

	public static List<String> getAllMatches(String text, String regex,
			String pattern1, String pattern2) {
		List<String> matches = new ArrayList<>();
		Matcher m = Pattern.compile("(?=(" + regex + "))").matcher(text);
		while (m.find()) {
			if (m.group(1).contains(pattern1) || m.group(1).contains(pattern2))
				matches.add(m.group(1));
		}
		return matches;
	}

	public static List<String> getAllMatches(String text, String regex) {
		List<String> matches = new ArrayList<>();
		Matcher m = Pattern.compile("(?=(" + regex + "))").matcher(text);
		while (m.find()) {
			matches.add(m.group(1));
		}
		return matches;
	}

	public static int setNewTheme(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		int curTheme = sp.getInt(Preferences.PREF_THEME,
				AllNewsActivity.THEME_WHITE);
		int newTheme = AllNewsActivity.THEME_WHITE;

		if (curTheme == AllNewsActivity.THEME_WHITE)
			newTheme = AllNewsActivity.THEME_DARK;

		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(Preferences.PREF_THEME, newTheme);
		editor.commit();
		return newTheme;
	}

	public static void showVirtualKeyBoard(View edit, Activity mContext) {
		InputMethodManager mgr = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
	}

	public static void hideVirtualKeyBoard(Activity mContext) {
		InputMethodManager mgr = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(
				mContext.getCurrentFocus().getWindowToken(), 0);
	}

	public static void openUsWindow(Context context) {
		Statistic.sendStatistic(context, Statistic.CATEGORY_CLICK,
				Statistic.ACTION_SHOW_US_FROM_LIST, "", 0L);

		Intent intent = new Intent(context, AboutScreen.class);

		context.startActivity(intent);
	}

	public static void openHintsWindow(Context context) {
		Statistic.sendStatistic(context, Statistic.CATEGORY_CLICK,
				Statistic.ACTION_SHOW_HINTS_FROM_LIST, "", 0L);

		Intent intent = new Intent(context, HintsActivity.class);

		context.startActivity(intent);
	}

	public static void setLanguage(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String lang = sp.getString(Preferences.PREF_LANGUAGE, "null");
		String defLang = Locale.getDefault().getLanguage();

		Locale myLocale = new Locale(Preferences.LANGUAGE_EN);
		if (!lang.equals("null")) {
			myLocale = new Locale(lang);
		} else {
			if (!defLang.equals(Preferences.LANGUAGE_RU)
					&& !defLang.equals(Preferences.LANGUAGE_UA))
				defLang = Preferences.LANGUAGE_EN;

			myLocale = new Locale(defLang);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(Preferences.PREF_LANGUAGE, defLang);
			editor.commit();
		}

		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

	}

	public static String getDeviceId(Context context) {
		return Secure
				.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	public static void findViewsWithText(List<View> outViews, ViewGroup parent,
			String targetDescription) {
		if (parent == null || TextUtils.isEmpty(targetDescription)) {
			return;
		}
		final int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = parent.getChildAt(i);
			final CharSequence desc = child.getContentDescription();
			if (!TextUtils.isEmpty(desc)
					&& targetDescription.equals(desc.toString())) {
				outViews.add(child);
			} else if (child instanceof ViewGroup
					&& child.getVisibility() == View.VISIBLE) {
				findViewsWithText(outViews, (ViewGroup) child,
						targetDescription);
			}
		}
	}

	public static boolean checkSDCard() {
		Boolean isSDPresent = android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);
		if (!isSDPresent)
			DatabaseHelper.DB_PATH = "";
		return isSDPresent;
	}

	public static boolean isSDFree() {
		long SIZE_KB = 1024L;

		long SIZE_MB = SIZE_KB * SIZE_KB;
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		// long sdAvailSize = (long)stat.getFreeBytes();
		// double megaAvailable = sdAvailSize / 8*1024;
		long sdAvailSize = (long) stat.getAvailableBlocks()
				* (long) stat.getBlockSize();
		long megaAvailable = sdAvailSize / SIZE_MB;
		return megaAvailable > 50 ? true : false;
	}

	public static void openPrefWindow(Context context) {

		Intent intent = new Intent(context, Preferences.class);

		context.startActivity(intent);
	}

	public static ArrayList<Integer> getNewsIds(List<News> newsList) {
		ArrayList<Integer> list = new ArrayList<>();
		for (News news : newsList) {
			list.add(news.getNewsID());
		}
		return list;
	}
    public static ArrayList<Integer> getNewAppNodeID(List<NewApp> newAppsList){
        ArrayList<Integer> list = new ArrayList<>();
        for (NewApp newApp : newAppsList) {
            list.add(newApp.getNodeID());
        }
        return list;
    }

	public static void viewHashCode(Context context, String packageName) {
		try {
			Log.e("KeyHash:KeyHashKeyHash",
					"KeyHashKeyHashKeyHash");
			PackageInfo info = context.getPackageManager().getPackageInfo(
					 packageName, PackageManager.GET_SIGNATURES);
			for (android.content.pm.Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}// it debug zj9c3ibBg0UIQ0gMHJhPTrniLjM= //it
			// release NzTziXBZfUmY2Ne4kI1Qq+mtokA=

		} catch (NameNotFoundException ignored) {

		} catch (NoSuchAlgorithmException ignored) {

		}
	}
    public static long roundingToTenMinutes(long time){
        return Math.round( ( (double)time/(double)(10*60*1000) ) ) * (10*60*1000);
    }
}
