package com.allNews.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;

import com.allNews.managers.EWLoader;
import com.allNews.managers.MyPreferenceManager;
import com.squareup.picasso.Callback;

import gregory.network.rss.R;

public class FullImage extends Activity {
	public static final String URL_KEY = "URL_KEY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (MyPreferenceManager.getRotatePref(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		setContentView(R.layout.full_image);

		ImageView image = (ImageView) findViewById(R.id.fullImageView);
		String url = getIntent().getExtras().getString(URL_KEY);
		Callback callBack = new Callback() {
			
			@Override
			public void onSuccess() {
			//	Log.e("onSuccess", "onSuccess");
				
			}
			
			@Override
			public void onError() {
				onBackPressed();
				
			}
		};
		EWLoader.loadImg(this, url, image,   callBack );

	}
}
