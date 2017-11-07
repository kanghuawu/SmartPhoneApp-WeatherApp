package com.cmpe277.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitySwipeViewActivity extends FragmentActivity {
    public static final String TAG = "Weather";
    public static final String KEY_DATE = "date";
    public static final String KEY_WEATHER = "weather";
    public static final String KEY_HIGHEST_TEMPERATURE = "highesttemperature";
    public static final String KEY_LOWEST_TEMPERATURE = "lowesttemperature";
    private static final String[] KEY_DATA_ITEMS = {KEY_DATE, KEY_WEATHER, KEY_HIGHEST_TEMPERATURE, KEY_LOWEST_TEMPERATURE};
    private static final int[] KEY_LAYOUT_ITEMS = {R.id.forecast_unit_date, R.id.forecast_unit_weather, R.id.forecast_unit_highest_temperature, R.id.forecast_unit_lowest_temperature};

    static List<String> cityList;
    static int cityListSize;


    CitySwipePagerAdapter citySwipePagerAdapter;
    ViewPager mViewPager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.city_swipe_layout);
        citySwipePagerAdapter = new CitySwipePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(citySwipePagerAdapter);

        cityList = Setting.getCityList(CitySwipeViewActivity.this);
        cityListSize = cityList.size();

        Intent cityListIntent = getIntent();
        final int position = cityListIntent.getIntExtra(CityListActivity.KEY_POSITION, 0);
        mViewPager.setCurrentItem(position);
        citySwipePagerAdapter.notifyDataSetChanged();


    }


    public static class CitySwipePagerAdapter extends FragmentStatePagerAdapter {
        public CitySwipePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Log.i(TAG, "Swiping getting item " + i);
            Fragment fragment = new SingleCityFragment();
            Bundle args = new Bundle();
            args.putInt(CityListActivity.KEY_POSITION, i);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return cityListSize;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "City " + (position + 1);
        }
    }

    public static class SingleCityFragment extends Fragment {

        private List<Map<String, String>> hourlyData, dailyData;
        private SimpleAdapter hourlyAdapter, dailyAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.single_city_swipe_layout, container, false);
            Bundle args = getArguments();
            int position = args.getInt(CityListActivity.KEY_POSITION);
            ((TextView) rootView.findViewById(R.id.single_city_name)).setText(cityList.get(position));

            final String cityName = cityList.get(position);
            WeatherUpdater.updateCurrentWeatherForSingleCity(this, position, cityName);

            hourlyData = new ArrayList<>();
            dailyData = new ArrayList<>();
            final ListView hourlyForecastListView = rootView.findViewById(R.id.hourly_forecast_list);
            hourlyAdapter = new SimpleAdapter(this.getContext(), hourlyData, R.layout.forecast_element,
                    KEY_DATA_ITEMS, KEY_LAYOUT_ITEMS);
            hourlyForecastListView.setAdapter(hourlyAdapter);
            final ListView dailyForecastListView = rootView.findViewById(R.id.daily_forecast_list);
            dailyAdapter = new SimpleAdapter(this.getContext(), dailyData, R.layout.forecast_element,
                    KEY_DATA_ITEMS, KEY_LAYOUT_ITEMS);
            dailyForecastListView.setAdapter(dailyAdapter);

            WeatherUpdater.updateHourlyForecast(this, position, cityName);
            WeatherUpdater.updateDailyForecast(this, position, cityName);

            return rootView;
        }


        public void updateUIForCurrentWeather(final WeatherDataModel weatherData, int position) {
            final TextView cityName = this.getView().findViewById(R.id.single_city_name);
            final TextView weather = this.getView().findViewById(R.id.single_city_weather);
            final TextView temperature = this.getView().findViewById(R.id.single_city_temperature);
            cityName.setText(cityList.get(position));
            weather.setText(weatherData.getmWeatherCondition());
            temperature.setText(weatherData.getCurrentTemperature(Setting.getTemperatureType(this.getActivity())));

        }

        public void updateUIForHourlyForecast(final WeatherDataModel weatherData) {
            if (weatherData != null) {
                for (final WeatherDataModel singleHour: weatherData.getHourlyForecast()) {
                    final Map<String, String> data = new HashMap<>();
                    data.put(KEY_DATE, singleHour.getFormattedTime());
                    data.put(KEY_WEATHER, singleHour.getmWeatherCondition());
                    data.put(KEY_HIGHEST_TEMPERATURE, singleHour.getHighestTemperature(Setting.getTemperatureType(this.getActivity())));
                    data.put(KEY_LOWEST_TEMPERATURE, singleHour.getLowestTemperature(Setting.getTemperatureType(this.getActivity())));
                    hourlyData.add(data);
                }
                hourlyAdapter.notifyDataSetChanged();
            }
        }


        public void updateUIForDailyForecast(final WeatherDataModel weatherData) {
            if (weatherData != null) {
                for (final WeatherDataModel singleDay: weatherData.getDailyForecast()) {
                    final Map<String, String> data = new HashMap<>();
                    data.put(KEY_DATE, singleDay.getFormattedTime());
                    data.put(KEY_WEATHER, singleDay.getmWeatherCondition());
                    data.put(KEY_HIGHEST_TEMPERATURE, singleDay.getHighestTemperature(Setting.getTemperatureType(this.getActivity())));
                    data.put(KEY_LOWEST_TEMPERATURE, singleDay.getLowestTemperature(Setting.getTemperatureType(this.getActivity())));
                    dailyData.add(data);
                }
                dailyAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }



}
