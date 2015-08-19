package com.allNews.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allNews.utils.WeatherIconMapper;
import com.allNews.utils.WeatherUtil;
import com.allNews.weather.WeatherFragment;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.Weather;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;
import com.survivingwithandroid.weather.lib.util.LogUtils;
import com.survivingwithandroid.weather.lib.util.WindDirection;

import gregory.network.rss.R;

public class CurrentWeatherFragment extends WeatherFragment implements WeatherFragment.WeatherEventListener  {


    private SharedPreferences prefs;

    // UI elements
    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;
    private TextView unitTemp;
    private TextView hum;
    private ImageView imgView;
    private TextView tempMin;
    private TextView tempMax;
    private TextView sunset;
    private TextView sunrise;
    private TextView cloud;
    private TextView colorTextLine;
    private TextView rain;
    private LinearLayout currentWeatherLayout;



    private ContentLoadingProgressBar progress;


    private WeatherConfig config;

    public static CurrentWeatherFragment newInstance() {
        return new CurrentWeatherFragment();
    }
    public CurrentWeatherFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.weather_current_fr, container, false);
        cityText = (TextView) v.findViewById(R.id.location);
        temp = (TextView) v.findViewById(R.id.temp);
        condDescr = (TextView) v.findViewById(R.id.descrWeather);
        imgView = (ImageView) v.findViewById(R.id.imgWeather);
        hum = (TextView) v.findViewById(R.id.humidity);
        press = (TextView) v.findViewById(R.id.pressure);
        windSpeed = (TextView) v.findViewById(R.id.windSpeed);
        windDeg = (TextView) v.findViewById(R.id.windDeg);
        tempMin = (TextView) v.findViewById(R.id.tempMin);
        tempMax = (TextView) v.findViewById(R.id.tempMax);
        unitTemp = (TextView) v.findViewById(R.id.tempUnit);
        sunrise = (TextView) v.findViewById(R.id.sunrise);
        sunset = (TextView) v.findViewById(R.id.sunset);
        cloud = (TextView) v.findViewById(R.id.cloud);
        colorTextLine = (TextView) v.findViewById(R.id.lineTxt);
        rain = (TextView) v.findViewById(R.id.rain);
        progress = (ContentLoadingProgressBar) v.findViewById(R.id.progressCurrent);
        currentWeatherLayout = (LinearLayout) v.findViewById(R.id.currentWeatherLayout);




        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
        currentWeatherLayout.setVisibility(View.GONE);
        progress.show();


    }

    @Override
    public void onStop() {
        super.onStop();
        requestCompleted();

    }

    @Override
    public void refreshData() {
        refresh();
    }

    private void refresh() {
        config = new WeatherConfig();



     /*if (lat == 0 && lon == 0) {
            getListener().requestCompleted();
            return ;
        }*/

        config.lang = WeatherUtil.getLanguage("system");
        config.maxResult = 10;
        config.numDays = 7;
        config.ApiKey = "38310d7000ae5ad60e9a21c65d60cd22";


        String unit = prefs.getString("swa_temp_unit", "c");
        if (unit.equals("c"))
            config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
        else
            config.unitSystem = WeatherConfig.UNIT_SYSTEM.I;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());



            weatherClient.updateWeatherConfig(config);
        try {
            weatherClient.getCurrentCondition(new WeatherRequest(sp.getString(Preferences.CITY_ID, WeatherHeaderFragment.DEFAULT_WEATHER_CITY_ID)), new WeatherClient.WeatherEventListener() {
                @Override
                public void onWeatherRetrieved(CurrentWeather cWeather) {
                    if(getActivity() != null) {
                        Weather weather = cWeather.weather;
                        requestCompleted();
                        currentWeatherLayout.setVisibility(View.VISIBLE);
                        cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                        condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                        LogUtils.LOGD("SwA", "Temp [" + temp + "]");
                        LogUtils.LOGD("SwA", "Val [" + weather.temperature.getTemp() + "]");
                        temp.setText("" + ((int) weather.temperature.getTemp()));
                        unitTemp.setText(cWeather.getUnit().tempUnit);
                        colorTextLine.setBackgroundResource(WeatherUtil.getResource(weather.temperature.getTemp(), config));
                        hum.setText(weather.currentCondition.getHumidity() + "%");
                        tempMin.setText(weather.temperature.getMinTemp() + cWeather.getUnit().tempUnit);
                        tempMax.setText(weather.temperature.getMaxTemp() + cWeather.getUnit().tempUnit);
                        windSpeed.setText(weather.wind.getSpeed() + cWeather.getUnit().speedUnit);
                        windDeg.setText((int) weather.wind.getDeg() + "° (" + WindDirection.getDir((int) weather.wind.getDeg()) + ")");
                        press.setText(weather.currentCondition.getPressure() + cWeather.getUnit().pressureUnit);

                        sunrise.setText(WeatherUtil.convertDate(weather.location.getSunrise()));

                        sunset.setText(WeatherUtil.convertDate(weather.location.getSunset()));

                        imgView.setImageResource(WeatherIconMapper.getWeatherResource(weather.currentCondition.getIcon(), weather.currentCondition.getWeatherId()));

                        cloud.setText(weather.clouds.getPerc() + "%");

                        if (weather.rain[0].getTime() != null && weather.rain[0].getAmmount() != 0)
                            rain.setText(weather.rain[0].getTime() + ":" + weather.rain[0].getAmmount());
                        else
                            rain.setText("----");
                    }

                }

                @Override
                public void onWeatherError(WeatherLibException t) {
                    //WeatherDialog.createErrorDialog("Error parsing data. Please try again", MainActivity.this);
                  //  t.printStackTrace();
                    if (getActivity() != null)
                    requestCompleted();
                }


                @Override
                public void onConnectionError(Throwable t) {
                    //WeatherDialog.createErrorDialog("Error parsing data. Please try again", MainActivity.this);
                   // t.printStackTrace();
                    if (getActivity() != null)
                    requestCompleted();

                }
            });

        }catch (Throwable t){
            t.printStackTrace();
        }

    }

    @Override
    public void requestCompleted() {
        if (progress !=null) {
            progress.hide();
        }
    }
}