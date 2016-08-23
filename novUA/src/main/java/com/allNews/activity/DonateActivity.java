package com.allNews.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.allNews.application.App;
import com.allNews.managers.DialogManager;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import gregory.network.rss.R;


public class DonateActivity extends ActionBarActivity implements OnClickListener {

    private static final String SUBSCRIPTION_ID_1 = "donate_id_1";
	private static final String SUBSCRIPTION_ID_2 = "donate_id_2";
	private static final String SUBSCRIPTION_ID_5 = "donate_id_5";
	private static final String SUBSCRIPTION_ID_15 = "donate_id_15";
	private static final String SUBSCRIPTION_ID_50 = "donate_id_50";
	public static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs1SyP1dKcaDN97SlbEg5QrL/6Q0pNBAL8IzTzVGdsO1QSXaONSnc48s8c1FhpW52i6U4c/V0iITA0hcfZwRtACf0qbOyVtvXvs6WaTlP/Dr25ophQ3jjcENnplLN+wp6z3wDn9aWTA2O4mEIafwN43qiYjW/j0WrxglI+YB49L6nvSVNa204SeRkdWII+mFJ60tRq5Kbx/v/aMDzehaiyHDwecF5GiUMRtc8O1lMgeCHJj8u2WvLT5GwwgaKZ9XNHO3hrXFqLiJlqAq24cpElIVTP/a+p9+saeU21bXzs3H1rvSLmangHXrZAhg+ATsB6tAivmRJsgavw15CoFsEyQIDAQAB";
	private BillingProcessor bp;
	private  boolean readyToPurchase = false;
	ActionBar actionBar;

	private static final String LOG_TAG = "Billing";
	public static final String CHEK_PURCHASE = "Billing";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donation_layout);
		initActionBar();
		initView();
		initBilling();
	
	}

	private void initActionBar() {
		actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));




		}
	}

	private void initView() {
        Button btnDonate1 = (Button) findViewById(R.id.btnDonate_1);
        Button btnDonate2 = (Button) findViewById(R.id.btnDonate_2);
        Button btnDonate5 = (Button) findViewById(R.id.btnDonate_5);
        Button btnDonate15 = (Button) findViewById(R.id.btnDonate_15);
        Button btnDonate50 = (Button) findViewById(R.id.btnDonate_50);
		Button promoButton = (Button) findViewById(R.id.promoButton);
		
		btnDonate1.setOnClickListener(this);
		btnDonate2.setOnClickListener(this);
		btnDonate5.setOnClickListener(this);
		btnDonate15.setOnClickListener(this);
		btnDonate50.setOnClickListener(this);
		promoButton.setOnClickListener(this);

	}

    @Override
    protected void onResume() {

        super.onResume();
    }

    private void initBilling() {

		bp = new BillingProcessor(DonateActivity.this, LICENSE_KEY,
				new BillingProcessor.IBillingHandler() {

					@Override
					public void onPurchaseHistoryRestored() {
						for (String sku : bp.listOwnedSubscriptions()) {
							Log.d(LOG_TAG, "Owned Subscription: " + sku);
							if (sku != null){
								SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DonateActivity.this);
								SharedPreferences.Editor editor = sp.edit();
								editor.putBoolean(CHEK_PURCHASE, true);
								editor.apply();
								Toast.makeText(DonateActivity.this, "Ads disable", Toast.LENGTH_LONG).show();

							}
						}

					}

					@Override
					public void onProductPurchased(String productId,
												   TransactionDetails arg1) {
						Toast.makeText(DonateActivity.this,
								"onProductPurchased: " + productId,
								Toast.LENGTH_LONG).show();

					}

					@Override
					public void onBillingInitialized() {
						readyToPurchase = true;
						Log.d(LOG_TAG, "inited billing");
					}

					@Override
					public void onBillingError(int errorCode, Throwable arg1) {

						Log.d(LOG_TAG, "inited billing error");
					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!bp.handleActivityResult(requestCode, resultCode, data))
			super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onDestroy() {
		if (bp != null)
			bp.release();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (!readyToPurchase) {
			Toast.makeText(this, "Billing not initialized.",
					Toast.LENGTH_LONG).show();
			return;
		}
		switch (v.getId()) {
		case R.id.btnDonate_1:
			
			bp.subscribe(this,SUBSCRIPTION_ID_1);
			
			break;
		case R.id.btnDonate_2:
			bp.subscribe(this,SUBSCRIPTION_ID_2);
			break;
		case R.id.btnDonate_5:
			bp.subscribe(this,SUBSCRIPTION_ID_5);
			break;
		case R.id.btnDonate_15:
			bp.subscribe(this,SUBSCRIPTION_ID_15);
			break;
		case R.id.btnDonate_50:
			bp.subscribe(this,SUBSCRIPTION_ID_50);
			break;
			case R.id.promoButton:
				DialogManager.showPromoCodeDialog(this);
				break;

		default:
			break;
		}

	}
		
	}

	


