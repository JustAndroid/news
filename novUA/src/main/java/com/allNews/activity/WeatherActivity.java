package com.allNews.activity;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.allNews.managers.MyPreferenceManager;
import com.allNews.weather.WeatherFragment;
import com.allNews.web.Statistic;
import com.allNews.web.WeatherClientDefault;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;

import static gregory.network.rss.R.id.currentWeatherFrag;

public class WeatherActivity extends ActionBarActivity implements TabListener, WeatherFragment.WeatherEventListener {


    private List<Fragment> activeFragment = new ArrayList<Fragment>();

    private MenuItem refreshItem;
    private AdView mAdView;
    private AdView adView1;
    private CurrentWeatherFragment cf;
    private ForecastWeatherFragment ff;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.weather_main);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!sp.getBoolean(DonateActivity.CHEK_PURCHASE, false) && !sp.getBoolean(Preferences.PREF_PROMO, false)) {
            mAdView = (AdView) findViewById(R.id.adView);
            adView1 = (AdView) findViewById(R.id.adView2);
            AdRequest adRequest = new AdRequest.Builder().build();
            AdRequest adRequest1 = new AdRequest.Builder().build();

            mAdView.loadAd(adRequest);
            adView1.loadAd(adRequest1);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
            adView1.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView1.setVisibility(View.VISIBLE);
                }
            });
        }
            Statistic.sendStatistic(this, Statistic.CATEGORY_FULL_WEATHER, Statistic.ACTION_CLICK_SHOW_WEATHER, "", 0L);

        // Let's verify if we are using a smartphone or a tablet
      // View v = findViewById(R.id.currentWeatherFrag);

     //   if (v != null)
     //       isThereForecast = true;


        /*
        // Prior to 1.5.0
        client = WeatherClientDefault.getInstance();
        client.init(getApplicationContext());
        Log.d("App", "Client ["+client+"]");
        // Let's create the WeatherProvider
        config = new WeatherConfig();
        config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
        IWeatherProvider provider = null;
        try {
            //provider = WeatherProviderFactory.createProvider(new YahooProviderType(), config);
             provider = WeatherProviderFactory.createProvider(new OpenweathermapProviderType(), config);
            // provider = WeatherProviderFactory.createProvider(new WeatherUndergroundProviderType(), config);
             client.setProvider(provider);
        }
        catch (Throwable t) {
            t.printStackTrace();
            // There's a problem
        }
        */

        int curTheme = MyPreferenceManager.getCurrentTheme(this);

        setSupportProgressBarIndeterminate(true);
        setSupportProgressBarVisibility(true);
        boolean isThereForecast = false;
        if (isThereForecast) {
            ActionBar bar = getSupportActionBar();
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            //ActionBar.Tab tab = bar.newTab();
            createTab(R.string.tab_current, bar);
            createTab(R.string.tab_forecast, bar);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
        else {

            ActionBar bar = getSupportActionBar();
            if (bar != null) {
                bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                        curTheme == AllNewsActivity.THEME_DARK ? R.color.bgActionBarNight
                                : R.color.bgActionBarDay)));

            if (!getResources().getBoolean(R.bool.need_event)) {
                Spannable actionBarTitle = new SpannableString(getResources()
                        .getString(R.string.app_full_name));

                actionBarTitle
                        .setSpan(
                                new ForegroundColorSpan(
                                        curTheme == AllNewsActivity.THEME_WHITE ? getResources()
                                                .getColor(R.color.black)
                                                : getResources().getColor(
                                                R.color.txtGrey)), 0,
                                actionBarTitle.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                bar.setTitle(actionBarTitle);

                bar.setDisplayShowTitleEnabled(true);
                bar.setHomeButtonEnabled(true);
                bar.setDisplayShowHomeEnabled(true);


                bar.setDisplayUseLogoEnabled(false);


                return;
            }
            }



            if(savedInstanceState == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setTransitionStyle(R.style.fragmentAnim);
                cf = CurrentWeatherFragment.newInstance();
                ft.add(currentWeatherFrag, cf, "currentWeather");

                ff = ForecastWeatherFragment.newInstance();
                ft.add(R.id.forecastWeatherFrag, ff, "forecastWeather");
                ft.commitAllowingStateLoss();

            }
        }


    }



    @Override
    protected void onStart() {
        super.onStart();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action) {
            Intent i = new Intent();
            i.setClass(this, WeatherPreferenceActivity.class);
            startActivity(i);
        }
        else if (id == R.id.action_refresh) {
            refreshItem = item;
            FragmentManager fm = getFragmentManager();

            if (isThereForecast) {

                CurrentWeatherFragment frag  = (CurrentWeatherFragment) fm.findFragmentById(currentWeatherFrag);
                if (frag instanceof CurrentWeatherFragment) {
                    showProgressBar(true);
                    ((CurrentWeatherFragment) frag).refreshData();
                } else {
                    ForecastWeatherFragment frag1 = (ForecastWeatherFragment) fm.findFragmentById(R.id.forecastWeatherFrag);
                    if (frag1 instanceof ForecastWeatherFragment)
                        ((ForecastWeatherFragment) frag1).refreshData();
                }
            } else {
                WeatherFragment f = (WeatherFragment) activeFragment.get(currentPos);
                if (f != null)
                    f.refreshData();
            }
        }
        else if (id == R.id.action_share) {
            String playStoreLink = "https://play.google.com/store/apps/details?id=" +
                    getPackageName();

            String msg = "There's a new weather app in the play store. Look here " + playStoreLink;
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void createTab(int labelId, ActionBar bar) {
        ActionBar.Tab tab = bar.newTab();
        tab.setText(getResources().getString(labelId));
        tab.setTabListener(this);
        bar.addTab(tab);

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        int pos = tab.getPosition();
        int currentPos = pos;

        Fragment f = null;
        fragmentTransaction.setTransitionStyle(R.style.fragmentAnim);

        if (activeFragment.size() > pos)
            f = activeFragment.get(pos);

        if (f == null) {
            if (pos == 0) {
                // Current weather
                CurrentWeatherFragment cwf = CurrentWeatherFragment.newInstance();
                fragmentTransaction.add(android.R.id.content, cwf);
                activeFragment.add(cwf);
            }
            else if (pos == 1) {
                ForecastWeatherFragment fwf = ForecastWeatherFragment.newInstance();
                fragmentTransaction.add(android.R.id.content, fwf);
                activeFragment.add(fwf);
            }
        }
        else {
            fragmentTransaction.add(android.R.id.content, f);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.d("SwaA", "Tab unselected");
        int pos = tab.getPosition();
        Fragment f = activeFragment.get(pos);
        fragmentTransaction.remove(f);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.d("Swa", "Tab reselected");
    }

    @Override
    public void requestCompleted() {
       // showProgressBar(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        WeatherClientDefault weatherClientDefault = WeatherClientDefault.getInstance();
        weatherClientDefault.cancelAllRequest();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAdView!=null && adView1 !=null) {
            mAdView.pause();
            adView1.pause();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mAdView!=null && adView1 !=null) {
            mAdView.resume();
            adView1.resume();
        }
    }


    @SuppressLint("NewApi")
    private void showProgressBar(boolean showIt) {
        Log.d("SwA", "ShowProgress ["+showIt+"]");
        setSupportProgressBarVisibility(showIt);
        if (refreshItem == null)
            return ;
        if (showIt){
        }
        else
            refreshItem.setActionView(null);
    }

    @Override
    protected void onDestroy() {
        if(mAdView!=null && adView1 !=null) {
            mAdView.destroy();
            adView1.destroy();
        }
        super.onDestroy();
    }
}

