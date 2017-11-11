package com.cmpe277.weather;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class Setting {

    private static final String PREF_TEMPERATURE_TYPE = "temperatureType";
    private static final String PREF_CITY = "city";
    private static final WeatherDataModel.TemperatureType[] TEMPERATURE_TYPE_VALUES = WeatherDataModel.TemperatureType.values();
    private static final WeatherDataModel.TemperatureType DEFAULT_TEMPERATURE_TYPE = WeatherDataModel.TemperatureType.FAHRENHEIT;
    private static final String DEFAULT_CITY = null; //"San Jose";

    public static WeatherDataModel.TemperatureType getTemperatureType(final Activity activity) {
        final SharedPreferences preferences = activity.getSharedPreferences(PREF_TEMPERATURE_TYPE, MODE_PRIVATE);
        return TEMPERATURE_TYPE_VALUES[preferences.getInt(PREF_TEMPERATURE_TYPE, DEFAULT_TEMPERATURE_TYPE.ordinal())];
    }

    public static void changeTemperatureType(final Activity activity, final WeatherDataModel.TemperatureType type) {
        final SharedPreferences preferences = activity.getSharedPreferences(PREF_TEMPERATURE_TYPE, MODE_PRIVATE);
        preferences.edit()
                .putInt(PREF_TEMPERATURE_TYPE, type.ordinal())
                .apply();

    }

    public static List<String> getCityList(final Activity activity) {
        final SharedPreferences preferences = activity.getSharedPreferences(PREF_CITY, MODE_PRIVATE);
        List<String> cities = new ArrayList<>();
        for (int i = 0; i < preferences.getInt(PREF_CITY + "size", 0); i++) {
            cities.add(preferences.getString(PREF_CITY + i, null));
        }
        return cities;
    }

    private static  void addCityList(final Activity activity, final String city) {
        final SharedPreferences preferences = activity.getSharedPreferences(PREF_CITY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int size = preferences.getInt(PREF_CITY + "size", 0);
        editor.putInt(PREF_CITY + "size", size + 1);
        editor.putString(PREF_CITY + size, city);
        editor.apply();
    }

    public static void addCityList(final Activity activity, final List<String> cities) {
        final SharedPreferences preferences = activity.getSharedPreferences(PREF_CITY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putInt(PREF_CITY + "size", cities.size());
        for (int i = 0; i < cities.size(); i++) {
            editor.putString(PREF_CITY + i, cities.get(i));
        }
        editor.apply();
    }

    public static void removeCityList(final Activity activity) {
        final SharedPreferences preferences = activity.getSharedPreferences(PREF_CITY, MODE_PRIVATE);
        preferences.edit().clear().apply();
    }
}
