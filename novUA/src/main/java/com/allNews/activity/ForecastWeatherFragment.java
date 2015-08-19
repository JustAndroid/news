package com.allNews.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allNews.adapter.WeatherAdapter;
import com.allNews.weather.WeatherFragment;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import gregory.network.rss.R;
import it.sephiroth.android.library.widget.HListView;

public class ForecastWeatherFragment extends WeatherFragment {

    private SharedPreferences prefs;
    private HListView forecastList;
    private ContentLoadingProgressBar progress;
    private WeatherAdapter adp;


    public static ForecastWeatherFragment newInstance() {
        return new ForecastWeatherFragment();
    }

    public ForecastWeatherFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.weather_forecast_fragment, container, false);
        forecastList = (HListView) v.findViewById(R.id.forecastDays);
        progress = (ContentLoadingProgressBar) v.findViewById(R.id.progressForecast);

        return v;

    }

    public void refreshData() {

        refresh();
    }

    @Override
    public void onStart() {
        super.onStart();

        refresh();
    }

    private void refresh() {
        // Update forecast
        progress.show();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        try {
            weatherClient.getForecastWeather(new WeatherRequest(prefs.getString(Preferences.CITY_ID, WeatherHeaderFragment.DEFAULT_WEATHER_CITY_ID)), new WeatherClient.ForecastWeatherEventListener() {
                @Override
                public void onWeatherRetrieved(WeatherForecast forecast) {
                    if(getActivity() != null) {
                        adp = new WeatherAdapter(forecast, getActivity());
                        forecastList.setVisibility(View.VISIBLE);
                        forecastList.setAdapter(adp);
                        progress.hide();
                    }
                }

                @Override
                public void onWeatherError(WeatherLibException t) {
                    if(progress != null) {
                        progress.hide();
                    }
                }

                @Override
                public void onConnectionError(Throwable t) {
                    if (progress != null) {
                        progress.hide();
                    }
                 // Toast.makeText(getActivity(), getResources().getString(R.string.weather_toast_msg), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        if(progress != null) {
            progress.hide();
        }
        super.onStop();
    }
}