package com.cmpe277.weather;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherUpdater {

    public static final String API_WEATHER =         "http://api.openweathermap.org/data/2.5/weather";
    public static final String API_FORECAST_HOURLY = "http://api.openweathermap.org/data/2.5/forecast";
    public static final String API_FORECAST_DAILY =  "http://api.openweathermap.org/data/2.5/forecast/daily";

    /**
     * App ID to use OpenWeather data
     */
    static final String APP_ID = "e72ca729af228beabd5d20e3b7749713";


//    public static void updateCurrentWeather(final WeatherController weatherController, RequestParams params) {
//        sendRequest(API_WEATHER, weatherController, params);
//    }
//
//    public static void updateHourlyForecast(final WeatherController weatherController, RequestParams params) {
//        sendRequest(API_FORECAST_HOURLY, weatherController, params);
//    }
//
//    public static void updateDailyForecast(final WeatherController weatherController, RequestParams params) {
//        sendRequest(API_FORECAST_DAILY, weatherController, params);
//    }
//
//    private static void sendRequest(final String apiUrl, final WeatherController weatherController, RequestParams params) {
//        AsyncHttpClient client = new AsyncHttpClient();
//
//        client.get(apiUrl, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.d("Weather App", "Success! JSON: " + response.toString());
//                WeatherDataModel weatherData = WeatherDataModel.weatherFromJson(response);
//                weatherController.updateUI(weatherData, apiUrl);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.e("Weather App", "Fail " + throwable.toString());
//                Log.e("Weather App", "Status code " + statusCode);
////                Toast.makeText(weatherController, "Request Failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public static void updateCurrentWeatherForCityList(final CityListActivity cityList, final RequestParams params, int position) {
        sendRequestForCityList(position, cityList, params);
    }

    private static void sendRequestForCityList(final int position, final CityListActivity cityList, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        params.put("appid", APP_ID);
        client.get(API_WEATHER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("Weather App", "Success! JSON: " + response.toString());
                WeatherDataModel weatherData = WeatherDataModel.weatherFromJson(response);
                cityList.updateUI(weatherData, position);
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
