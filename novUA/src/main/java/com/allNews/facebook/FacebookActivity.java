package com.allNews.facebook;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.allNews.web.Statistic;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import gregory.network.rss.R;

public class FacebookActivity extends FragmentActivity {
	private static final List<String> PERMISSIONS = Collections.singletonList("publish_actions");
	CallbackManager callbackManager;
	ShareDialog shareDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_layout);
		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						shareDialog = new ShareDialog((Activity) getApplicationContext());
						// this part is optional
						shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
							@Override
							public void onSuccess(Sharer.Result result) {
								if (ShareDialog.canShow(ShareLinkContent.class)) {
									ShareLinkContent linkContent = new ShareLinkContent.Builder()
											.setContentTitle("Hello Facebook")
											.setContentDescription(
													"The 'Hello Facebook' sample  showcases simple Facebook integration")
											.setContentUrl(Uri.parse("http://developers.facebook.com/android"))
											.build();

									shareDialog.show(linkContent);
									//ShareApi.share(content, null);
								}
							}

							@Override
							public void onCancel() {

							}

							@Override
							public void onError(FacebookException e) {

							}

						});
					}

					@Override
					public void onCancel() {
						// App code
					}

					@Override
					public void onError(FacebookException exception) {
						// App code
					}
				});
		// start Facebook Login


	}



	protected void postOk() {
		Toast.makeText(FacebookActivity.this, R.string.facebook_post_published,
				Toast.LENGTH_LONG).show();
		Statistic.sendStatistic(this, Statistic.CATEGORY_CLICK,
				Statistic.ACTION_CLICK_POST_FACEBOOK, "", 0L);

	}

	private boolean isSubsetOf(Collection<String> superset) {
		for (String string : FacebookActivity.PERMISSIONS) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}
}
