package com.allNews.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allNews.application.App;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.WeatherIconMapper;
import com.allNews.utils.WeatherUtil;
import com.allNews.weather.WeatherFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.Weather;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import gregory.network.rss.R;

public class WeatherHeaderFragment extends WeatherFragment implements ConnectionCallbacks, OnConnectionFailedListener {

    private TextView cityText;
    private TextView temp;
    private TextView colorTextLine;
    private ImageView imgView;
    private TextView unitTemp;
    private TextView condDescr;
    private Bitmap bitmap;
    private long curTime;
    private TextView searchTxt;
    private ImageView menu_ic;

    private final String CITY_TEXT = "cityText";
    private final String TEMP = "temp";
    private final String IMG_VIEW = "imgView";
    private final String UNIT_TEMP = "unitTemp";
    private final String COND_DESCR = "condDescr";
    private final String CUR_TIME = "cur_time";
    private final String CUR_TOAST_TIME = "CUR_TOAST_TIME";
    private final long timeDelay = 30 * 60000; // 30 min
    public static final String DEFAULT_WEATHER_CITY_ID = App.getContext().getResources().getString(R.string.city_weather_id);

    private WeatherConfig config;

    private GoogleApiClient mGoogleApiClient;
    public static Location mLocation;
    public static double lat;
    public static double lon;

    SharedPreferences sharedPreferences ;
    Activity mActivity;


    public static WeatherHeaderFragment newInstance() {

        return new WeatherHeaderFragment();
    }

    public WeatherHeaderFragment() {
    }





    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        mActivity = activity;

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buildGoogleApiClient();

        View v = inflater.inflate(R.layout.weather_header_fr, container, false);
        cityText = (TextView) v.findViewById(R.id.location);
        temp = (TextView) v.findViewById(R.id.temp);
        colorTextLine = (TextView) v.findViewById(R.id.lineTxt);
        imgView = (ImageView) v.findViewById(R.id.imgWeather);
        unitTemp = (TextView) v.findViewById(R.id.tempUnit);
        condDescr = (TextView) v.findViewById(R.id.descrWeather);

        menu_ic = (ImageView) v.findViewById(R.id.menu_img);



        return v;
    }

    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
final Handler handler = new Handler();
       new Thread(new Runnable() {
           @Override
           public void run() {


               handler.post(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap1;
                    try {

                        bitmap1 = getImageFromString(sharedPreferences.getString(IMG_VIEW, ""));
                        imgView.setImageBitmap(bitmap1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cityText.setText(sharedPreferences.getString(CITY_TEXT, "N/A"));
                    condDescr.setText(sharedPreferences.getString(COND_DESCR, "N/A"));
                    temp.setText(sharedPreferences.getString(TEMP, "N/A"));
                    unitTemp.setText(sharedPreferences.getString(UNIT_TEMP, "c"));

                }
            });
           }
       }).start();




        menu_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity.getApplicationContext(), SearchLocationActivity.class);
                startActivity(intent);
            }
        });

        refresh();
    }


    @Override
    public void refreshData() {
        refresh();
    }

    @Override
    public void onDetach() {
        super.onDetach();


    }

    private void refresh() {
        config = new WeatherConfig();


        config.lang = WeatherUtil.getLanguage("system");
        config.maxResult = 10;
        config.numDays = 7;
        config.ApiKey = "38310d7000ae5ad60e9a21c65d60cd22";

        weatherClient.updateWeatherConfig(config);

        if (isTimePassed() || sharedPreferences.getBoolean("isUpdateLocation", false)) {
            try {

                     weatherClient.getCurrentCondition(new WeatherRequest(sharedPreferences.getString(Preferences.CITY_ID, DEFAULT_WEATHER_CITY_ID)), new WeatherClient.WeatherEventListener() {
                    @Override
                    public void onWeatherRetrieved(CurrentWeather cWeather) {
                        if(getActivity() !=null && cWeather !=null) {
                            Weather weather = cWeather.weather;
                            getListener().requestCompleted();
                            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");

                            temp.setText("" + ((int) weather.temperature.getTemp()));
                            unitTemp.setText(cWeather.getUnit().tempUnit);
                            colorTextLine.setBackgroundResource(WeatherUtil.getResource(weather.temperature.getTemp(), config));

                            imgView.setImageResource(WeatherIconMapper.getWeatherResource(weather.currentCondition.getIcon(), weather.currentCondition.getWeatherId()));
                            bitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                            curTime = System.currentTimeMillis();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.putBoolean("isUpdateLocation", false);
                                    edit.putLong(CUR_TIME, curTime);
                                    edit.putString(CITY_TEXT, cityText.getText().toString());
                                    edit.putString(COND_DESCR, condDescr.getText().toString());
                                    edit.putString(TEMP, temp.getText().toString());
                                    try {
                                        edit.putString(IMG_VIEW, getStringImage(bitmap));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    edit.apply();
                                }
                            }).start();

                        }

                    }

                /*
                client.getDefaultProviderImage(weather.currentCondition.getIcon(), new WeatherClient.WeatherImageListener() {
                    @Override
                    public void onImageReady(Bitmap image) {
                        imgView.setImageBitmap(image);
                    }
                });
                */



                    @Override
                    public void onWeatherError(WeatherLibException t) {
                        //WeatherDialog.createErrorDialog("Error parsing data. Please try again", MainActivity.this);
                        if (getActivity() !=null)
                        getListener().requestCompleted();
                    }


                    @Override
                    public void onConnectionError(Throwable t) {
                        //WeatherDialog.createErrorDialog("Error parsing data. Please try again", MainActivity.this);
                        if (getActivity() !=null)
                        getListener().requestCompleted();
                    }
                });
            } catch (Throwable t) {
                Log.e("Weather", t.toString());
            }
        }
    }

    private boolean isTimePassed() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        return (timeDelay + sharedPreferences.getLong(CUR_TIME, 0)) <= System.currentTimeMillis();
    }

    private String getStringImage(Bitmap image) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    private Bitmap getImageFromString(String imgString) throws Exception{

        byte[] decodedByte = Base64.decode(imgString, 0);

        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            lat = mLocation.getLatitude();
            lon = mLocation.getLongitude();
            if (getActivity() != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MyPreferenceManager.saveLatLocation(getActivity(), lat);
                        MyPreferenceManager.saveLonLocation(getActivity(), lon);
                        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());

                        List<Address> addresses = null;
                        try {
                            addresses = gcd.getFromLocation(lat, lon, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if ((addresses != null ? addresses.size() : 0) > 0) {

                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            if (addresses != null) {
                                edit.putString(Preferences.COUNTRY_CODE, addresses.get(0).getCountryCode());
                                edit.apply();
                            }

                        }
                    }
                }).start();

            }

            if (isTimePassed() && lat != 0.0d && lon != 0.0d) {
                weatherClient.getCurrentCondition(new WeatherRequest(lon, lat), new WeatherClient.WeatherEventListener() {
                    @Override
                    public void onWeatherRetrieved(CurrentWeather currentWeather) {
                        if (getActivity() != null && currentWeather != null) {
                            Weather weather = currentWeather.weather;
                            getListener().requestCompleted();
                            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");

                            temp.setText("" + ((int) weather.temperature.getTemp()));
                            unitTemp.setText(currentWeather.getUnit().tempUnit);
                            colorTextLine.setBackgroundResource(WeatherUtil.getResource(weather.temperature.getTemp(), config));

                            imgView.setImageResource(WeatherIconMapper.getWeatherResource(weather.currentCondition.getIcon(), weather.currentCondition.getWeatherId()));

                            curTime = System.currentTimeMillis();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    if (((BitmapDrawable) imgView.getDrawable()).getBitmap() != null) {
                                        bitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                                        try {
                                            edit.putString(IMG_VIEW, getStringImage(bitmap));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }


                                    edit.putLong(CUR_TIME, curTime);
                                    edit.putString(CITY_TEXT, cityText.getText().toString());
                                    edit.putString(COND_DESCR, condDescr.getText().toString());
                                    edit.putString(TEMP, temp.getText().toString());

                                    edit.apply();
                                }
                            }).start();

                        }
                    }


                    @Override
                    public void onWeatherError(WeatherLibException e) {
                        if (getActivity() !=null)
                        getListener().requestCompleted();
                    }

                    @Override
                    public void onConnectionError(Throwable throwable) {
                        if (getActivity() !=null) {
                            getListener().requestCompleted();
                        }
                    }
                });
            }



        } else {

            if (((30 * 60000 + sharedPreferences.getLong(CUR_TOAST_TIME, 0)) <= System.currentTimeMillis())) {
                Toast.makeText(getActivity(), getResources().getString(R.string.location_msg), Toast.LENGTH_LONG).show();
                long curToastTime = System.currentTimeMillis();
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putLong(CUR_TOAST_TIME, curToastTime);
                edit.apply();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (((30 * 60000 + sharedPreferences.getLong(CUR_TOAST_TIME, 0)) <= System.currentTimeMillis())) {
            if(getActivity() != null) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.location_msg), Toast.LENGTH_LONG).show();
            }
            long curToastTime = System.currentTimeMillis();
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putLong(CUR_TOAST_TIME, curToastTime);
            edit.apply();

        }

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}
