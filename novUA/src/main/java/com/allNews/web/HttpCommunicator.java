package com.allNews.web;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class HttpCommunicator {

	private static final String TAG = "HttpCommunicator";

    public HttpCommunicator(Context context) {
        Context mContext = context;
	}

 

	 

	public String sendGetRequest( String urlPath) {

		Log.d(TAG, "Url: " + urlPath);

		HttpGet get = new HttpGet( urlPath);
	 
		get.setHeader("Content-Type", "application-json");
	 	get.addHeader("Accept-Encoding", "gzip");

		return getResponse(get);
	}

	private String getResponse(HttpUriRequest get) {

		String response = "";
		try {
			HttpClient client = new DefaultHttpClient();
			  HttpParams httpParams = client.getParams();
		        HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
		        HttpConnectionParams.setSoTimeout(httpParams, 20000);
			HttpResponse responsePOST = client.execute(get);

			Log.i(TAG,
					"Status code: "
							+ Integer.toString(responsePOST.getStatusLine()
									.getStatusCode()));

			if (responsePOST.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED
					|| responsePOST.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
 
				
				InputStream is = responsePOST.getEntity().getContent();
				try {
					 
						 is = new GZIPInputStream(is);
			
						 
						
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "utf-8"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
						
					}
					is.close();

					response = sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d(TAG, "Response: " + response);
		return response;
	}
	
}