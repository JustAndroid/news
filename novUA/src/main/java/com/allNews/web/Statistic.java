package com.allNews.web;

import android.content.Context;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import java.util.HashMap;
import java.util.Map;

import gregory.network.rss.R;

public class Statistic {
	public static final String CATEGORY_CLICK = "click";
	public static final String CATEGORY_NEWS = "news";
	public static final String CATEGORY_EVENTS = "events";
	public static final String CATEGORY_CATEGORY = "categories";
	public static final String CATEGORY_NEW_APP = "new app";
	public static final String CATEGORY_ERROR = "error";
	public static final String CATEGORY_FULL_WEATHER = "weather";
    public static final String CATEGORY_PRESS_RELEASE_B2B = "press release B2B";

	public static final String ACTION_ERROR_DOWNLOAD_NEWS = "download news";
	public static final String ACTION_ERROR_INET = "internet error";
	public static final String ACTION_ERROR_INET_AFTER_ERROR = "internet error after server error";
	public static final String ACTION_SHOW_US_FROM_LIST = "click in us in newslis";
	public static final String ACTION_SHOW_HINTS_FROM_LIST = "click in hint in newslist";
	public static final String ACTION_CLICK_BTN_DONATE = "Button donate";
	public static final String ACTION_CLICK_BTN_UPDATE = "Button update";
	public static final String ACTION_CLICK_BTN_SHARE = "Button share";
	public static final String ACTION_CLICK_BTN_WEB = "Button readInWeb";
	public static final String ACTION_CLICK_BTN_DOWNLOAD_APP = "Button downloadApp";
	public static final String ACTION_CLICK_LINK = "link to news in our App";
	public static final String ACTION_CLICK_BTN_OUR_APP = "To our app";
	public static final String ACTION_CLICK_BTN_RATE = "rate dialog";
	public static final String ACTION_CLICK_BTN_FEEDBACK = "feedback";
	public static final String ACTION_CLICK_EVENT = "show event";
	public static final String ACTION_CLICK_ALL_EVENT = "show event list";
	public static final String ACTION_CLICK_ALL_NEW_APP = "show new App list";
	public static final String ACTION_CLICK_LINK_NEW_APP = "click link in NewApp";
	public static final String ACTION_CLICK_EVENT_BUY = "event click buy button";
	public static final String ACTION_CLICK_EVENT_CREATE = "event click create(+)";
	public static final String ACTION_CLICK_EVENT_HELP = "event click help(?)";
	public static final String ACTION_CLICK_NEWAPP_FROM_TOP20 = "click to newApp from Top20";
	public static final String ACTION_CLICK_MORE_NEWAPP_FROM_TOP20 = "click to see more newApp from Top20";
	public static final String ACTION_CLICK_POST_FACEBOOK = "post news to facebook";
	public static final String ACTION_CLICK_SHOW_WEATHER = "click weather banner";

	public static final String LABEL_ERROR_DNS_ALLNEWS_SERVER = "Domain Name Resolving error (for all-news.com.ua)";
	public static final String LABEL_ERROR_INET_ALLNEWS_SERVER = "all-news server connect error";
	public static final String LABEL_ERROR_INET_GOOGLE = "google connect error";
	public static final String LABEL_ERROR_INET_CONNECTION = "no internet connection";
	public static final String LABEL_SHARE_FROM_NEWS = "share app from news";
	public static final String LABEL_SHARE_FROM_HINT = "share app from hint";
	public static final String LABEL_SHARE_FROM_SETTINGS = "share app from settings";
	public static final String LABEL_SHARE_FROM_HINTS = "share app from hints";
	public static final String LABEL_SHARE = "show share";
	public static final String LABEL_READ_IN_WEB = "show readInWeb";
	public static final String LABEL_RATE_US = "Button rate us";
	public static final String LABEL_RATE_WISH = "Button rate wish";
	public static final String LABEL_RATE_LATER = "Button rate later";

	public static final String LABEL_RATE_NEVER = "Button rate never";



    public static void sendErrorStatistic(Context context, String category,
			String action, String label, Long value) {
		FlurryAgent.onStartSession(context,
				context.getResources().getString(R.string.fluri_id));

		Tracker v3EasyTracker = EasyTracker.getInstance(context);

		v3EasyTracker.set(Fields.SCREEN_NAME, "no screen");

		v3EasyTracker.send(MapBuilder.createAppView().build());
		v3EasyTracker.send(MapBuilder.createEvent(category, action, label, value).build());

	}

	public static void sendStatistic(Context context, String category,
			String action, String label, Long value) {

		Map<String, String> parameters = new HashMap<>();
		parameters.put(action, label);
		FlurryAgent.logEvent(category, parameters);

		EasyTracker.getInstance(context).send(
				MapBuilder.createEvent(category, action, label, value).build());

	}
}
