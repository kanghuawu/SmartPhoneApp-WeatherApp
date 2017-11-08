package com.cmpe277.weather;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherUpdater {

    /**
     * OpenWeather API URL
     */
    public static final String API_WEATHER =         "http://api.openweathermap.org/data/2.5/weather";
    public static final String API_FORECAST_HOURLY = "http://api.openweathermap.org/data/2.5/forecast";
    public static final String API_FORECAST_DAILY =  "http://api.openweathermap.org/data/2.5/forecast/daily";

    /**
     * App ID to use OpenWeather data
     */
    static final String APP_ID = "e72ca729af228beabd5d20e3b7749713";

    /**
     * OpenWeather API parameter
     */
    public static final String PARAM_APPID = "appid";
    public static final String PARAM_COUNT = "cnt";
    public static final String PARAM_CITY = "q";

    /**
     * OpenWeather API forecast count we needed
     */
    private static final int HOURLY_FORECAST_COUNT = 8;
    private static final int DAILY_FORECAST_COUNT = 5;


    public static void updateHourlyForecast(final CitySwipeViewActivity.SingleCityFragment city, final CityModel cityModel, final String cityName) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(PARAM_CITY, cityName);
        params.put(PARAM_APPID, APP_ID);
        params.put(PARAM_COUNT, HOURLY_FORECAST_COUNT);
        client.get(API_FORECAST_HOURLY, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("Weather App", "Success! JSON: " + response.toString());
                WeatherDataModel weatherData = null;
                try {
                    weatherData = WeatherDataModel.hourlyForecastFromJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                city.updateUIForHourlyForecast(cityModel, weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("Weather App", "Fail " + throwable.toString());
                Log.e("Weather App", "Status code " + statusCode);
            }
        });
    }

    public static void updateDailyForecast(final CitySwipeViewActivity.SingleCityFragment city, final CityModel cityModel, final String cityName) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(PARAM_CITY, cityName);
        params.put(PARAM_APPID, APP_ID);
        params.put(PARAM_COUNT, DAILY_FORECAST_COUNT);
        client.get(API_FORECAST_DAILY, params, new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i("Weather App", "Success! JSON: " + response.toString());
            WeatherDataModel weatherData = null;
            try {
                weatherData = WeatherDataModel.dailyForecastFromJson(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            city.updateUIForDailyForecast(cityModel, weatherData);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.e("Weather App", "Fail " + throwable.toString());
            Log.e("Weather App", "Status code " + statusCode);
        }
    });
}


    public static void updateCurrentWeatherForSingleCity(final CityModel cityModel, final CitySwipeViewActivity.SingleCityFragment singleCityFragment, final int position, final String cityName) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(PARAM_CITY, cityName);
        params.put(PARAM_APPID, APP_ID);
        client.get(API_WEATHER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("Weather App", "Success! JSON: " + response.toString());
                WeatherDataModel weatherData = WeatherDataModel.weatherFromJson(response);
                singleCityFragment.updateUIForCurrentWeather(weatherData, position);
                cityModel.updateLocalizedTime(singleCityFragment, weatherData.getmLatitude(), weatherData.getmLongitude());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("Weather App", "Fail " + throwable.toString());
                Log.e("Weather App", "Status code " + statusCode);
            }
        });
    }

    public static void updateCurrentWeatherForCityList(final CityModel cityModel, final CityListActivity cityList, final int position, final String cityName) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(PARAM_CITY, cityName);
        params.put(PARAM_APPID, APP_ID);
        client.get(API_WEATHER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("Weather App", "Success! JSON: " + response.toString());
                WeatherDataModel weatherData = WeatherDataModel.weatherFromJson(response);
                cityList.updateUI(weatherData, position);
                cityModel.updateLocalizedTime(cityList, weatherData.getmLatitude(), weatherData.getmLongitude());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("Weather App", "Fail " + throwable.toString());
                Log.e("Weather App", "Status code " + statusCode);
//                Toast.makeText(weatherController, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
