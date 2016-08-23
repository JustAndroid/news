package com.allNews.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allNews.activity.AllNewsActivity;
import com.allNews.activity.NewsCollectionActivity;
import com.allNews.activity.NewsFragment;
import com.allNews.application.App;
import com.allNews.managers.EWLoader;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;
import com.allNews.web.Statistic;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;

public class NewsContentView extends LinearLayout {

	private static final String NEWS_URL_MEDIA_PATTERN = "<a.*?</a>";
	private static final String NEWS_URL_PATTERN = "<a.*?>";
	private static final String NEWS_IMAGE_PATTERN = "type=\"pict\"";
	private static final String NEWS_IMAGE_PATTERN2 = "img src=";
	private static final String NEWS_VIDEO_PATTERN = "type=\"movie\"";
	private static final String NEWS_PATTERN = "newsID=";
	private static final String NEWS_YOUTUB_PATTERN = "://www.youtube.com";

	private Context context;
	RequestQueue requestQueue;
	public boolean isNewApp=false;
	private String newsName = "";
	public NewsContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public NewsContentView(Context context) {
		super(context);
		this.context = context;
	}




	public void setIsNewAppMark() {
		isNewApp = true;
	}

	public void setnewsName(String newsName) {
		this.newsName = newsName;
	}

	public void setContent(String newsFullContent) {
		removeAllViews();
		List<String> imageMatches = Utils
				.getAllMatches(newsFullContent, NEWS_URL_MEDIA_PATTERN,
						NEWS_IMAGE_PATTERN, NEWS_YOUTUB_PATTERN);

		if (imageMatches.size() > 0) {
			try {

				for (int i = 0; i < imageMatches.size(); i++) {

					setTextAndImage(newsFullContent, imageMatches, i);

				}
			} catch (Exception e) {
				removeAllViews();
				setText(newsFullContent);
			}

		} else
			setText(newsFullContent);
	}
	public void setContentNewApp (String newsFullContent) {
		removeAllViews();
		try {
			Document document = Jsoup.parse(newsFullContent);
			Elements img = document.select("img[src]");
			List<String> imageMatches = new ArrayList<>();
			for (Element element : img ){
				String imgSrc = element.attr("src");
				imageMatches.add(imgSrc);
			}



			if (imageMatches.size() > 0) {


				for (int i = 0; i < imageMatches.size(); i++) {

					setTextAndImageNewApp(newsFullContent, imageMatches, i);

				}


			} else
				setTextNewApp(newsFullContent);
		} catch (Exception e) {
			removeAllViews();
			setTextNewApp(newsFullContent);
		}
	}

	private void setTextAndImage(String newsFullContent,
								 List<String> imageMatches, int i) {
		String match = imageMatches.get(i);
		String matchPrevious = null;
		if (i > 0)
			matchPrevious = imageMatches.get(i - 1);
		String imageUrl;
		String content = "";
		if (i == 0) {
			content = newsFullContent.substring(0,
					newsFullContent.indexOf(match));
			setText(content);
			if (match.contains(NEWS_YOUTUB_PATTERN)) {
				loadYoutubThumbnail(match);
			} else {
				imageUrl = getUrl(match);
				setImage(imageUrl);
			}
		} else {
			if (match.contains(NEWS_YOUTUB_PATTERN)) {
				if (matchPrevious != null) {
					content = newsFullContent.substring(
							newsFullContent.indexOf(matchPrevious),
							newsFullContent.indexOf(match));
				}
				setText(content);
				loadYoutubThumbnail(match);
			} else {
				if (matchPrevious != null) {
					content = newsFullContent.substring(
							newsFullContent.indexOf(matchPrevious)
									+ matchPrevious.length(),
							newsFullContent.indexOf(match));
				}
				setText(content);
				imageUrl = getUrl(match);
				setImage(imageUrl);
			}
		}

		// setText(content);
		// setImage(imageUrl);
		if (i == imageMatches.size() - 1) {
			if (match.contains(NEWS_YOUTUB_PATTERN))
				content = newsFullContent.substring(
						newsFullContent.indexOf(match),
						newsFullContent.length());
			else
				content = newsFullContent.substring(
						newsFullContent.indexOf(match) + match.length(),
						newsFullContent.length());
			setText(content);
		}

	}
	private void setTextAndImageNewApp(String newsFullContent,
									   List<String> imageMatches, int i) {
		String match = imageMatches.get(i);
		String matchPrevious = null;
		if (i > 0)
			matchPrevious = imageMatches.get(i - 1);
		String imageUrl = "";
		String content = "";
		if (i == 0) {
			content = newsFullContent.substring(0,
					newsFullContent.indexOf(match));
			content.replaceAll("\"/>", "");
			setTextNewApp(content);
			if (match.contains(NEWS_YOUTUB_PATTERN)) {
				loadYoutubThumbnail(match);
			} else {
				imageUrl = imageMatches.get(i);
				setImage(imageUrl);
			}
		} else {
			if (match.contains(NEWS_YOUTUB_PATTERN)) {
				if (matchPrevious != null) {
					content = newsFullContent.substring(
							newsFullContent.indexOf(matchPrevious),
							newsFullContent.indexOf(match));
				}
				setTextNewApp(content);
				loadYoutubThumbnail(match);
			} else {
				if (matchPrevious != null) {
					content = newsFullContent.substring(
							newsFullContent.indexOf(matchPrevious)
									+ matchPrevious.length(),
							newsFullContent.indexOf(match));
				}
				setTextNewApp(content);
				imageUrl = imageMatches.get(i);
				setImage(imageUrl);
			}
		}

		// setText(content);
		// setImage(imageUrl);
		if (i == imageMatches.size() - 1) {
			if (match.contains(NEWS_YOUTUB_PATTERN))
				content = newsFullContent.substring(
						newsFullContent.indexOf(match),
						newsFullContent.length());
			else
				content = newsFullContent.substring(
						newsFullContent.indexOf(match) + match.length(),
						newsFullContent.length());
			setTextNewApp(content);
		}

	}

	private void loadYoutubThumbnail(String match) {
		final ImageView image = new ImageView(context);
		addView(image);

		try {
			final String youtubUrl = getUrl(match)
					.replace("embed/", "watch?v=").replace("?wmode=opaque", "")
					.replace("feature=player_embedded&amp;", "")
					.replace("?rel=0", "");

			String url = "http://www.youtube.com/oembed?format=json&url="
					+ youtubUrl;
			// Log.e("url", ""+url);?rel=0
			requestQueue = App.getRequestQueue(getContext());
			Listener<JSONObject> listener = new Listener<JSONObject>() {

				@Override
				public void onResponse(final JSONObject response) {
					// Log.e("onResponse", ""+response);
					if (image != null && response.has("thumbnail_url")) {
						try {
							String thumbnail_url = response
									.getString("thumbnail_url");
							setImage(thumbnail_url, image);
							image.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									playVideo(context, youtubUrl);
								}
							});
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			};
			requestQueue.add(new JsonObjectRequest(url, null, listener, null));
		} catch (Exception e) {

		}
	}

	@SuppressLint("NewApi")
	private void setTextNewApp(String content) {
		content = content.replaceAll("\" />", "");
		TextView txtText = new TextView(context);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			txtText.setTextIsSelectable(true);
        /*Document doc = Jsoup.parse(content);
        String txt = doc.text();
        txt = txt.replaceAll("\" />", "");*/
		txtText.setText(Html.fromHtml(content));
		txtText.setTextSize(MyPreferenceManager.getTextSize(context));
		txtText.setMovementMethod(ArrowKeyMovementMethod.getInstance());
		//Linkify.addLinks(txtText, Linkify.ALL);
		txtText.setAutoLinkMask(Linkify.ALL);
		txtText.setMovementMethod(LinkMovementMethod.getInstance());
		CharSequence text = txtText.getText();
		if (text instanceof Spannable) {

			List<String> newsMatches = Utils.getAllMatches(content,
					NEWS_URL_PATTERN, NEWS_PATTERN);
			List<String> videoMatches = Utils.getAllMatches(content,
					NEWS_URL_MEDIA_PATTERN, NEWS_VIDEO_PATTERN);
			int end = text.length();
			Spannable spn = (Spannable) txtText.getText();
			URLSpan[] urls = spn.getSpans(0, end, URLSpan.class);
			SpannableStringBuilder style = new SpannableStringBuilder(text);
			style.clearSpans();
			for (URLSpan url : urls) {
				String newsID = null;
				boolean isVideo = isVideoURL(videoMatches, url.getURL());
				if (!isVideo)
					newsID = getNewsIdIfExist(newsMatches, url.getURL());

				CustomerTextClick click = new CustomerTextClick(url.getURL(),
						newsID, isVideo, isNewApp, newsName);
				style.setSpan(click, spn.getSpanStart(url),
						spn.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			txtText.setText(style);
		}
		if ( MyPreferenceManager.getCurrentTheme(context) == AllNewsActivity.THEME_DARK)
			txtText.setTextColor(getResources().getColor(R.color.newsTextNight));

		addView(txtText);

	}
	@SuppressLint("NewApi")
	private void setText(String content) {
		TextView txtText = new TextView(context);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			txtText.setTextIsSelectable(true);
		txtText.setText(Html.fromHtml(content));
		txtText.setTextSize(MyPreferenceManager.getTextSize(context));
		//	txtText.setMovementMethod(ArrowKeyMovementMethod.getInstance());
		//	Linkify.addLinks(txtText, Linkify.ALL);
		txtText.setAutoLinkMask(Linkify.ALL);
		// txtText.setMovementMethod(LinkMovementMethod.getInstance());
		CharSequence text = txtText.getText();
		if (text instanceof Spannable) {

			List<String> newsMatches = Utils.getAllMatches(content,
					NEWS_URL_PATTERN, NEWS_PATTERN);
			List<String> videoMatches = Utils.getAllMatches(content,
					NEWS_URL_MEDIA_PATTERN, NEWS_VIDEO_PATTERN);
			int end = text.length();
			Spannable spn = (Spannable) txtText.getText();
			URLSpan[] urls = spn.getSpans(0, end, URLSpan.class);
			SpannableStringBuilder style = new SpannableStringBuilder(text);
			style.clearSpans();
			for (URLSpan url : urls) {
				String newsID = null;
				boolean isVideo = isVideoURL(videoMatches, url.getURL());
				if (!isVideo)
					newsID = getNewsIdIfExist(newsMatches, url.getURL());

				CustomerTextClick click = new CustomerTextClick(url.getURL(),
						newsID, isVideo, isNewApp, newsName);
				style.setSpan(click, spn.getSpanStart(url),
						spn.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			try {
				txtText.setText(style);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ( MyPreferenceManager.getCurrentTheme(context) == AllNewsActivity.THEME_DARK)
			txtText.setTextColor(getResources().getColor(R.color.newsTextNight));

		addView(txtText);

	}

	private void setImage(final String imageUrl) {
		if (Utils.isUrlValid(imageUrl)) {
			ResizableImageView image = new ResizableImageView(context);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			image.setLayoutParams(params );
			image.setAdjustViewBounds(true);
			image.setScaleType(ScaleType.FIT_XY);
			NewsFragment.setOpenFullImageListener(image, imageUrl);

			addView(image,params);
			setImage(imageUrl, image);

		}
	}



	private void setImage(String imageUrl, ImageView image) {
		if (Utils.isUrlValid(imageUrl)) {
			EWLoader.loadImg(getContext().getApplicationContext(), imageUrl, image);
		}
	}

	private String getUrl(String match) {
		String patern1 = "<a href=\"";
		String patern2 = "\" type=";
		//int start = match.lastIndexOf(patern1);
		return match.substring(match.lastIndexOf(patern1) + patern1.length(),
				match.indexOf(patern2));
		//	return match.substring(match.indexOf(patern1) + patern1.length(),
		//			match.indexOf(patern2));

	}

	/*
	 * private String getImageUrl(String match) { // String patern =
	 * NEWS_IMAGE_PATTERN + ">"; String patern1 ="<a href=\""; String patern2
	 * ="\" type="; return match.substring(match.indexOf(patern1) +
	 * patern1.length(), match.indexOf(patern2));
	 * 
	 * }
	 * 
	 * private String getVideoUrl(String match) { String patern =
	 * NEWS_VIDEO_PATTERN + ">"; return match.substring(match.indexOf(patern) +
	 * patern.length(), match.indexOf("</a>"));
	 * 
	 * }
	 */
	private boolean isVideoURL(List<String> videoMatches, String url) {
		try {
			url = url.replace("&", "&amp;");
			if (videoMatches != null && videoMatches.size() > 0)
				for (String match : videoMatches) {
					if (match.contains(url)
							&& match.contains(NEWS_VIDEO_PATTERN)) {
						return true;
					}

				}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	private String getNewsIdIfExist(List<String> matches, String url) {
		try {
			url = url.replace("&", "&amp;");
			if (matches != null && matches.size() > 0)
				for (String match : matches) {
					if (match.contains(url) && match.contains(NEWS_PATTERN)) {

						return match.substring(match.indexOf(NEWS_PATTERN))
								.replace(NEWS_PATTERN, "").replace(">", "");
					}

				}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	private static class CustomerTextClick extends ClickableSpan {

		private String mUrl;
		private String newsID;
		private boolean isVideo;
		private boolean isNewApp;
		private String newsName;
		public CustomerTextClick(String url, String newsID, boolean isVideo, boolean isNewApp, String newsName) {
			mUrl = url;
			this.newsID = newsID;
			this.isVideo = isVideo;
			this.isNewApp = isNewApp;
			this.newsName = newsName;
		}

		@Override
		public void onClick(View widget) {

			if (isVideo) {
				playVideo(widget.getContext(), mUrl);
			} else if (newsID != null) {
				{
					Log.e("newsID", newsID);
					try {
						Integer.parseInt(newsID);
						showNews(widget.getContext(), newsID);
					} catch (Exception e) {
						openInBrowser(widget.getContext(), mUrl, isNewApp, newsName);
					}
				}

			} else {
				openInBrowser(widget.getContext(), mUrl, isNewApp, newsName);
			}
		}

	}

	private static void playVideo(Context context, String mUrl) {
		try {
			if (mUrl.contains(NEWS_YOUTUB_PATTERN) && mUrl.contains("embed/"))
				mUrl = mUrl.replace("embed/", "watch?v=");
			Uri uri = Uri.parse(mUrl);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if (!mUrl.contains(NEWS_YOUTUB_PATTERN))
				intent.setDataAndType(uri, "video/*");
			((FragmentActivity) context).startActivity(intent);

		} catch (Exception e) {
			openInBrowser(context, mUrl, false, "");
		}
	}

	private static void showNews(Context context, String newsID) {
		Statistic.sendStatistic(context, Statistic.CATEGORY_CLICK,
				Statistic.ACTION_CLICK_LINK, newsID, 0L);
		//ManagerNews.setNewsToHistory(context, Integer.parseInt(newsID), false);

		Intent intent = new Intent(context, NewsCollectionActivity.class);
		intent.putExtra(NewsCollectionActivity.NEWS_ID_KEY,
				Integer.parseInt(newsID));

		((FragmentActivity) context).startActivityForResult(intent, 1);
		((Activity) context).finish();
	}

	private static void openInBrowser(Context context, String mUrl, boolean isNewApp, String newsName) {
		if (Utils.isUrlValid(mUrl))
			try {
				if(isNewApp){
					Log.e("openInBrowser " + newsName, mUrl);
					Statistic.sendStatistic(context,
							Statistic.CATEGORY_NEW_APP,
							newsName, mUrl, 0l);
				}
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(mUrl));
				context.startActivity(browserIntent);
			} catch (Exception e) {

			}
	}
}
