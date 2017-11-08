package com.cmpe277.weather;

import android.support.annotation.NonNull;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Nefel on 2017/11/8.
 */

public class LocalizedTimeUpdater {

    private static final String API_GOOGLE_TIMEZONE = "https://maps.googleapis.com/maps/api/timezone/json";
     //?key=AIzaSyCt91scA2ZQvnerbpmS6M3uBwTuGiQB9iE&location=35.6895,139.6917&timestamp=1458000000

    private static final String PARAM_API_KEY = "key";
    private static final String PARAM_LOCATION = "location";
    private static final String PARAM_TIMESTAMP = "timestamp";

    private static final String GOOGLE_TIMEZONE_API_KEY = "AIzaSyCt91scA2ZQvnerbpmS6M3uBwTuGiQB9iE";


    public static RequestParams byParams(final CityModel cityModel, final String timestamp) {
        RequestParams params = new RequestParams();
        params.put(PARAM_API_KEY, GOOGLE_TIMEZONE_API_KEY);
        params.put(PARAM_LOCATION, cityModel.getLatitude() + "," + cityModel.getLongitude());
        params.put(PARAM_TIMESTAMP, timestamp.substring(0, timestamp.length()-3));
        return params;
    }

    public static RequestParams byParams(String latitude, String longitude, String timestamp) {
        RequestParams params = new RequestParams();
        params.put(PARAM_API_KEY, GOOGLE_TIMEZONE_API_KEY);
        params.put(PARAM_LOCATION, latitude + "," + longitude);
        params.put(PARAM_TIMESTAMP, timestamp.substring(0, timestamp.length()-3));
        return params;
    }

    public static void updateDateByLocationForCityList(final CityListActivity cityListActivity, final CityModel cityModel, final RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_GOOGLE_TIMEZONE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    final String timeZoneId = response.getString("timeZoneId");
                    cityModel.updateTimezone(cityListActivity, timeZoneId);
                    cityListActivity.updateUITime(cityModel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("Weather App", "Fail " + throwable.toString());
                Log.e("Weather App", "Status code " + statusCode);
            }
        });
    }


    public static void updateDateByLocationForCityView(final CitySwipeViewActivity.SingleCityFragment singleCityFragment, final CityModel cityModel, final RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_GOOGLE_TIMEZONE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    final String timeZoneId = response.getString("timeZoneId");
                    cityModel.updateTimezone(singleCityFragment, timeZoneId);
                    cityModel.updateForecast(singleCityFragment);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("Weather App", "Fail " + throwable.toString());
                Log.e("Weather App", "Status code " + statusCode);
            }
        });
    }

}
