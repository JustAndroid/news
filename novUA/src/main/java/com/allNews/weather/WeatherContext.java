package com.allNews.weather;

import android.content.Context;

import com.allNews.web.WeatherClientDefault;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.exception.WeatherProviderInstantiationException;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;

public class WeatherContext {
    private static WeatherContext me;
    private WeatherClient client;

    private WeatherContext() {}

    public static WeatherContext getInstance() {
        if (me == null) {
            me = new WeatherContext();
        }

        return me;
    }

    public WeatherClient getClient(Context ctx) {
        if (client != null) {
            return client;
        }else {

            try {
                client = new WeatherClient.ClientBuilder()
                        .attach(ctx)
                        .config(new WeatherConfig())
                        .provider(new OpenweathermapProviderType())
                        .httpClient(WeatherClientDefault.class)
                        .build();
            } catch (WeatherProviderInstantiationException e) {
                e.printStackTrace();
            }

            return client;
        }
    }
}
