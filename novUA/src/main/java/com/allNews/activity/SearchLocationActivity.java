package com.allNews.activity;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allNews.adapter.CityAdapter;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;
import com.allNews.weather.WeatherContext;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.City;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;

public class SearchLocationActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static double lat;
    public static double lon;
    private ProgressBar progressBar;
    private CityAdapter adp;
    private WeatherClient client;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public static boolean isUpdateLocation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_location_activity);
        buildGoogleApiClient();
        initActionBar();

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);


        client = WeatherContext.getInstance().getClient(this);

        Log.d("App", "Client [" + client + "]");

        ListView cityListView = (ListView) findViewById(R.id.cityList);

        adp = new CityAdapter(SearchLocationActivity.this, new ArrayList<City>());

        cityListView.setAdapter(adp);

        ImageView searchView = (ImageView) findViewById(R.id.imgSearch);
        final EditText edt = (EditText) findViewById(R.id.cityEdtText);


        edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    final String encoded;
                    try {
                        encoded = URLEncoder.encode(v.getText().toString(), "UTF-8");
                        search(encoded);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Utils.hideVirtualKeyBoard(SearchLocationActivity.this);
                    return true;
                }

                return false;
            }
        });


        searchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String encoded ;
                try {
                    encoded = URLEncoder.encode(edt.getEditableText().toString(), "UTF-8");
                    search(encoded);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                City city = (City) parent.getItemAtPosition(pos);
                editor.putString("cityid", city.getId());
                editor.putString("cityName", city.getName());
                editor.putString("country", city.getCountry());
                editor.putBoolean("isUpdateLocation", true);
                editor.apply();

                NavUtils.navigateUpFromSameTask(SearchLocationActivity.this);
            }
        });

        ImageView locImg = (ImageView) findViewById(R.id.imgLocationSearch);
        locImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.hideVirtualKeyBoard(SearchLocationActivity.this);
                if (mGoogleApiClient.isConnected()&& mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.disconnect();
                }
                if (mLastLocation == null) {
                    mGoogleApiClient.connect();
                    progressBar.setVisibility(View.VISIBLE);
                }

            }

        });

    }

    private void initActionBar() {
        int curTheme = MyPreferenceManager.getCurrentTheme(this);
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
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void search(String pattern) {
        progressBar.setVisibility(View.VISIBLE);
        client.searchCity(pattern, new WeatherClient.CityEventListener() {
            @Override
            public void onCityListRetrieved(List<City> cityList) {
                progressBar.setVisibility(View.GONE);
                adp.setCityList(cityList);
                adp.notifyDataSetChanged();
            }

            @Override
            public void onWeatherError(WeatherLibException t) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onConnectionError(Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
            client.searchCity(lat, lon, new WeatherClient.CityEventListener() {

                @Override
                public void onCityListRetrieved(List<City> cityList) {
                    progressBar.setVisibility(View.GONE);
                    adp.setCityList(cityList);
                    adp.notifyDataSetChanged();
                    Utils.hideVirtualKeyBoard(SearchLocationActivity.this);
                }

                @Override
                public void onWeatherError(WeatherLibException wle) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onConnectionError(Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        Toast.makeText(getApplicationContext(), "onConnectionSuspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_msg), Toast.LENGTH_LONG).show();
    }
}