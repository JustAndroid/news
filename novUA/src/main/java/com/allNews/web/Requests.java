package com.allNews.web;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

import com.allNews.data.History;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import gregory.network.rss.R;


public class Requests {

    public static JsonArrayRequest getRequest(String url,
			Listener<JSONArray> listener, ErrorListener errorListener) {
		JsonArrayRequest req = new JsonArrayRequest(Method.GET, url, null,
				listener, errorListener) {
			@Override
			public HashMap<String, String> getParams() {
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("Content-Type", "application-json");
				params.put("charset", "utf-8");
				params.put("Accept-Encoding", "gzip");

				return params;
			}

		};
        int MY_SOCKET_TIMEOUT_MS = 5000;
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		return req;
	}
    public static JsonObjectRequest getOjectRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, listener, errorListener) {
            @Override
            public HashMap<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();

                params.put("Content-Type", "application-json");
                params.put("charset", "utf-8");
                params.put("Accept-Encoding", "gzip");

                return params;
            }
        };
        int MY_SOCKET_TIMEOUT_MS = 5000;
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return request;
    }

	public static StringRequest getStringRequest(String url,
			Listener<String> listener, ErrorListener errorListener) {
		StringRequest req = new StringRequest(Method.GET, url, listener,
				errorListener) {
			@Override
			public HashMap<String, String> getParams() {
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("Content-Type", "application-json");
				params.put("charset", "utf-8");
				params.put("Accept-Encoding", "gzip");

				return params;
			}

		};
        int MY_SOCKET_TIMEOUT_MS = 5000;
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		return req;
	}

	public static StringRequest getRequestForNewsSynch(final Context context,
			List<History> newsToSynch, Listener<String> listener,
			ErrorListener errorListener) {

		String url = context.getResources().getString(R.string.url_domain)
				+ context.getResources()
						.getString(R.string.statistics_news_url);

		final JSONObject obj = new JSONObject();

		final JSONArray arrayNews = new JSONArray();
		try {

			for (History history : newsToSynch) {
				JSONObject objNews = new JSONObject();
				objNews.put("id", history.getNewsID());
				objNews.put("date", history.getTime().replace(" ", "%20"));
				arrayNews.put(objNews);
			}
			obj.put("sets", context.getResources().getString(R.string.app_id));
			obj.put("data", arrayNews);
		} catch (NotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        url = url + "&data=" + obj.toString();
		Log.e("urlforRead", url);
		StringRequest req = new StringRequest(Method.GET, url, listener,
				errorListener) {
			@Override
			public HashMap<String, String> getParams() throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("Content-Type", "application-json");
				params.put("charset", "utf-8");
				return params;
			}

		};
        int MY_SOCKET_TIMEOUT_MS = 5000;
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		return req;
	}

	public static JsonArrayRequest getJsonRequestForEvents(Context context,
			Listener<JSONArray> listener, ErrorListener errorListener) {

		String url = context.getResources().getString(R.string.event_url);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("app_id",
				context.getResources().getString(R.string.event_category));

		JsonArrayRequest req = new JsonArrayRequest(Method.POST, url,
				new JSONObject(params), listener, errorListener) {
			@Override
			public HashMap<String, String> getParams() {
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("Content-Type", "application/json");
				params.put("charset", "UTF-8");

				return params;
			}

		};
        int MY_SOCKET_TIMEOUT_MS = 5000;
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		return req;
	}

	public static StringRequest getRequestForLikesSynch(final Context context,
													   List<History> newsToSynch, Listener<String> listener,
													   ErrorListener errorListener) {

		String url = context.getResources().getString(R.string.url_domain)
				+ context.getResources()
				.getString(R.string.statistics_likes_url);

		final JSONObject obj = new JSONObject();

		final JSONArray arrayNews = new JSONArray();
		try {

			for (History history : newsToSynch) {
				JSONObject objNews = new JSONObject();
				objNews.put("id", history.getNewsID());
				objNews.put("date", history.getTime().replace(" ", "%20"));
				arrayNews.put(objNews);
			}
			obj.put("sets", context.getResources().getString(R.string.app_id));
			obj.put("data", arrayNews);
		} catch (NotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		url = url + "&data=" + obj.toString();
		Log.e("urlForLikes", url);
		StringRequest req = new StringRequest(Method.GET, url, listener,
				errorListener) {
			@Override
			public HashMap<String, String> getParams() throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("Content-Type", "application-json");
				params.put("charset", "utf-8");
				return params;
			}

		};
		int MY_SOCKET_TIMEOUT_MS = 5000;
		req.setRetryPolicy(new DefaultRetryPolicy(
				MY_SOCKET_TIMEOUT_MS,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		return req;
	}
	public static StringRequest getRequestForDislikesSynch(final Context context,
													   List<History> newsToSynch, Listener<String> listener,
													   ErrorListener errorListener) {

		String url = context.getResources().getString(R.string.url_domain)
				+ context.getResources()
				.getString(R.string.statistics_dislikes_url);

		final JSONObject obj = new JSONObject();

		final JSONArray arrayNews = new JSONArray();
		try {

			for (History history : newsToSynch) {
				JSONObject objNews = new JSONObject();
				objNews.put("id", history.getNewsID());
				objNews.put("date", history.getTime().replace(" ", "%20"));
				arrayNews.put(objNews);
			}
			obj.put("sets", context.getResources().getString(R.string.app_id));
			obj.put("data", arrayNews);
		} catch (NotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		url = url + "&data=" + obj.toString();
		Log.e("urlForDisLikes", url);
		StringRequest req = new StringRequest(Method.GET, url, listener,
				errorListener) {
			@Override
			public HashMap<String, String> getParams() throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("Content-Type", "application-json");
				params.put("charset", "utf-8");
				return params;
			}

		};
		int MY_SOCKET_TIMEOUT_MS = 5000;
		req.setRetryPolicy(new DefaultRetryPolicy(
				MY_SOCKET_TIMEOUT_MS,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		return req;
	}
	public static StringRequest getRequestLikes(final Context context, List<Integer> sourceIds
														, Listener<String> listener,
														ErrorListener errorListener) {

		String url = context.getResources().getString(R.string.url_domain)
				+ context.getResources()
				.getString(R.string.get_likes_url_);

		final JSONObject obj = new JSONObject();

		final JSONArray arrayNews = new JSONArray();
		for (Integer sourceId : sourceIds) {
			arrayNews.put(sourceId);
		}

		try {

			obj.put("data", arrayNews);
		} catch (JSONException e) {
			e.printStackTrace();
		}


		url = url + "&data=" + obj.toString();
		Log.e("urlForLikes", url);
		StringRequest req = new StringRequest(Method.GET, url, listener,
				errorListener) {
			@Override
			public HashMap<String, String> getParams() throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("Content-Type", "application-json");
				params.put("charset", "utf-8");
				return params;
			}
		};
		return req;
	}
}
