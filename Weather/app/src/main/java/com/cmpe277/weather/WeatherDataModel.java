package com.cmpe277.weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bondk on 11/3/17.
 */

public class WeatherDataModel {

    public static final String FAHRENHEIT_DEGREE = "°F";
    public static final String CELSIUS_DEGREE = "°C";
    /**
     * Data I need:
     * V 1. City name
     *      weather, /name
     * V 2. Day and Date of today (Thursday Oct 18)
     *      weather, /dt (?)
     * V 3. Weather status
     *      weather, /weather/main, /weather/id
     * V 4. Current, highest, lowest temperature
     *      weather, /main/temp, /main/temp_min, /main/temp_max
     * V 5. 1 day forecast every 3 hours, up to 24 hours. Weather status + temperature
     *      forecast,
     * V 6. 10 day forecast, starting from next day. daily weather (?)
     *      forecast daily,
     */

    private int mCurrTemp, mMinTemp, mMaxTemp;
    private String mCity;
    private Date mDate;
    private String mWeatherCondition;
    private List<WeatherDataModel> hourlyForecast;
    private List<WeatherDataModel> dailyForecast;
    private String mTime;
    private String mLatitude, mLongitude;

    /**
     * Parse weather data from json object.
     *
     * @param jsonObject
     * @return
     */
    public static WeatherDataModel weatherFromJson(final JSONObject jsonObject) {
        WeatherDataModel model = new WeatherDataModel();
        try {
            model.mCity = jsonObject.getString("name");
            model.mTime = jsonObject.getString("dt") + "000";
            model.mDate = new Date(Long.valueOf(model.mTime));
            model.mWeatherCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            model.mLongitude = jsonObject.getJSONObject("coord").getString("lon");
            model.mLatitude = jsonObject.getJSONObject("coord").getString("lat");
            model.mCurrTemp = extractTemperature(jsonObject, "temp");
            model.mMinTemp = extractTemperature(jsonObject, "temp_min");
            model.mMaxTemp = extractTemperature(jsonObject, "temp_max");
        } catch (JSONException e) {
            e.printStackTrace();
            model = null;
        }
        return model;
    }

    public static WeatherDataModel hourlyWeatherElement(final JSONObject jsonObject) {
        WeatherDataModel model = new WeatherDataModel();
        try {
            model.mTime = jsonObject.getString("dt") + "000";
            model.mDate = new Date(Long.valueOf(model.mTime));
            model.mWeatherCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            model.mCurrTemp = extractTemperature(jsonObject, "temp");
            model.mMinTemp = extractTemperature(jsonObject, "temp_min");
            model.mMaxTemp = extractTemperature(jsonObject, "temp_max");
        } catch (JSONException e) {
            e.printStackTrace();
            model = null;
        }
        return model;
    }

    public static WeatherDataModel dailyWeatherElement(final JSONObject jsonObject) {
        WeatherDataModel model = new WeatherDataModel();
        try {
            model.mTime = jsonObject.getString("dt") + "000";
            model.mDate = new Date(Long.valueOf(model.mTime));
            model.mWeatherCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            model.mCurrTemp = extractTemperatureFromDailyElement(jsonObject, "day");
            model.mMinTemp = extractTemperatureFromDailyElement(jsonObject, "min");
            model.mMaxTemp = extractTemperatureFromDailyElement(jsonObject, "max");
        } catch (JSONException e) {
            e.printStackTrace();
            model = null;
        }
        return model;
    }

    /**
     * Parse hourly forecast data from json and update into existing model.
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static WeatherDataModel hourlyForecastFromJson(final JSONObject jsonObject) throws JSONException {
        final WeatherDataModel model = new WeatherDataModel();
        List<WeatherDataModel> list = model.getHourlyForecast();
        for(int i=0; i<jsonObject.getJSONArray("list").length(); i++) {
            list.add(hourlyWeatherElement(jsonObject.getJSONArray("list").getJSONObject(i)));
        }
        return model;
    }

    /**
     * Parse daily forecast data from json and update into existing model.
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static WeatherDataModel dailyForecastFromJson(final JSONObject jsonObject) throws JSONException {
        final WeatherDataModel model = new WeatherDataModel();
        List<WeatherDataModel> list = model.getDailyForecast();
        for(int i=0; i<jsonObject.getJSONArray("list").length(); i++) {
            list.add(dailyWeatherElement(jsonObject.getJSONArray("list").getJSONObject(i)));
        }
        return model;
    }

    public Date getDate() {
        return mDate;
    }

    public String getCurrentTemperature(final TemperatureType type) {
        if (type.equals(TemperatureType.FAHRENHEIT)) {
            return Integer.toString(celsiusToFahrenheit(mCurrTemp)) + FAHRENHEIT_DEGREE;
        }
        return mCurrTemp + CELSIUS_DEGREE;
    }

    public String getLowestTemperature(final TemperatureType type) {
        if (type.equals(TemperatureType.FAHRENHEIT)) {
            return Integer.toString(celsiusToFahrenheit(mMinTemp)) + FAHRENHEIT_DEGREE;
        }
        return mMinTemp + CELSIUS_DEGREE;
    }

    public String getHighestTemperature(final TemperatureType type) {
        if (type.equals(TemperatureType.FAHRENHEIT)) {
            return Integer.toString(celsiusToFahrenheit(mMaxTemp)) + FAHRENHEIT_DEGREE;
        }
        return mMaxTemp + CELSIUS_DEGREE;
    }

    public String getmWeatherCondition() {
        return mWeatherCondition;
    }


    public List<WeatherDataModel> getHourlyForecast() {
        if (this.hourlyForecast == null) {
            this.hourlyForecast = new ArrayList<>();
        }
        return hourlyForecast;
    }

    public List<WeatherDataModel> getDailyForecast() {
        if (this.dailyForecast == null) {
            this.dailyForecast = new ArrayList<>();
        }
        return dailyForecast;
    }


    private static int extractTemperature(JSONObject jsonObject, final String name) throws JSONException {
        double tempResult = jsonObject.getJSONObject("main").getDouble(name) - 273.15;
        return (int) Math.rint(tempResult);
    }

    private static int extractTemperatureFromDailyElement(JSONObject jsonObject, final String name) throws JSONException {
        double tempResult = jsonObject.getJSONObject("temp").getDouble(name) - 273.15;
        return (int) Math.rint(tempResult);
    }

    private int celsiusToFahrenheit(int celsius) {
        return (int) Math.rint((celsius * 9 / 5.0) + 32);
    }

    public String getmTime() {
        return mTime;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public String getCity() {
        return mCity;
    }

    public enum TemperatureType{
        CELSIUS,
        FAHRENHEIT
    }

}
