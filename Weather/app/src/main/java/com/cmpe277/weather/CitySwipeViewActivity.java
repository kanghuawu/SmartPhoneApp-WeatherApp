package com.cmpe277.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.cmpe277.weather.task.TaskType;
import com.cmpe277.weather.task.UpdateCurrentWeatherTask;
import com.cmpe277.weather.task.UpdateForecastTask;
import com.cmpe277.weather.task.UpdateLocalizedTimeTask;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    private static final int[] KEY_LAYOUT_ITEMS = {R.id.forecast_unit_date, R.id.forecast_unit_weather,
            R.id.forecast_unit_highest_temperature, R.id.forecast_unit_lowest_temperature};

    static List<Map<String, Object>> dataList;
    static int cityListSize;
    static CityModel currentCity;

    CitySwipePagerAdapter citySwipePagerAdapter;
    ViewPager mViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.city_swipe_layout);
        citySwipePagerAdapter = new CitySwipePagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(citySwipePagerAdapter);


        Intent cityListIntent = getIntent();
        dataList = (ArrayList<Map<String, Object>>) cityListIntent.getSerializableExtra("dataList");
        currentCity = (CityModel) cityListIntent.getSerializableExtra("currentCity");
        cityListSize = dataList.size();
        Log.i(TAG, "City swip activity receive " + dataList);
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

        private CityModel cityModel;
        private SimpleAdapter hourlyAdapter, dailyAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.single_city_swipe_layout, container, false);
            Bundle args = getArguments();
            int position = args.getInt(CityListActivity.KEY_POSITION);

            Map<String, Object> city = dataList.get(position);
            final String cityName = city.get(CityListActivity.KEY_CITY).toString();
            ((TextView) rootView.findViewById(R.id.single_city_name)).setText(cityName);

            if (CityListActivity.currentCity != null && CityListActivity.currentCity.getCityName().equals(cityName)) {
                Log.i(TAG, "Finding current location");
                ((TextView) rootView.findViewById(R.id.single_city_here)).setText("You are here!");
            } else {
                ((TextView) rootView.findViewById(R.id.single_city_here)).setText("");
            }

            cityModel = new CityModel(cityName,
                    city.get(CityListActivity.KEY_LAT).toString(),
                    city.get(CityListActivity.KEY_LON).toString(),
                    position);
            final CityController controller = new CityController(cityModel, this);
            controller.addTask(new UpdateCurrentWeatherTask(controller, TaskType.CITY_VIEW));
            controller.addTask(new UpdateLocalizedTimeTask(controller, TaskType.CITY_VIEW));
            controller.addTask(new UpdateForecastTask(controller, TaskType.CITY_VIEW));
            controller.executeNext();

            final ListView hourlyForecastListView = rootView.findViewById(R.id.hourly_forecast_list);
            hourlyAdapter = new SimpleAdapter(this.getContext(), cityModel.getHourlyData(), R.layout.forecast_element,
                    KEY_DATA_ITEMS, KEY_LAYOUT_ITEMS);
            hourlyForecastListView.setAdapter(hourlyAdapter);
            final ListView dailyForecastListView = rootView.findViewById(R.id.daily_forecast_list);
            dailyAdapter = new SimpleAdapter(this.getContext(), cityModel.getDailyData(), R.layout.forecast_element,
                    KEY_DATA_ITEMS, KEY_LAYOUT_ITEMS);
            dailyForecastListView.setAdapter(dailyAdapter);
            return rootView;
        }


        public void updateUIForCurrentWeather(final WeatherDataModel weatherData, int position) {
            final TextView cityName = this.getView().findViewById(R.id.single_city_name);
            final TextView weather = this.getView().findViewById(R.id.single_city_weather);
            final TextView date = this.getView().findViewById(R.id.single_city_date);
            final TextView temperature = this.getView().findViewById(R.id.single_city_temperature);
            final TextView highestTemperature = this.getView().findViewById(R.id.single_city_highest_temperature);
            final TextView lowestTemperature = this.getView().findViewById(R.id.single_city_lowest_temperature);
            Map<String, Object> city = dataList.get(position);
            final String name = city.get(CityListActivity.KEY_CITY).toString();
            cityName.setText(name);

            weather.setText(weatherData.getmWeatherCondition());
            temperature.setText(weatherData.getCurrentTemperature(Setting.getTemperatureType(this.getActivity())));
            date.setText("-");
            highestTemperature.setText(weatherData.getHighestTemperature(Setting.getTemperatureType(this.getActivity())));
            lowestTemperature.setText(weatherData.getLowestTemperature(Setting.getTemperatureType(this.getActivity())));
        }

        public void updateUIForHourlyForecast(final CityModel cityModel, final WeatherDataModel weatherData) {
            if (weatherData != null) {
                for (final WeatherDataModel singleHour: weatherData.getHourlyForecast()) {
                    final Map<String, String> data = new HashMap<>();
                    data.put(KEY_DATE, cityModel.getFormattedTimeByTimestamp(singleHour.getmTime())); //singleHour.getFormattedTime());
                    data.put(KEY_WEATHER, singleHour.getmWeatherCondition());
                    data.put(KEY_HIGHEST_TEMPERATURE, singleHour.getHighestTemperature(Setting.getTemperatureType(this.getActivity())));
                    data.put(KEY_LOWEST_TEMPERATURE, singleHour.getLowestTemperature(Setting.getTemperatureType(this.getActivity())));
                    cityModel.getHourlyData().add(data);
                }
                hourlyAdapter.notifyDataSetChanged();
            }
        }


        public void updateUIForDailyForecast(final CityModel cityModel, final WeatherDataModel weatherData) {
            if (weatherData != null) {
                for (final WeatherDataModel singleDay: weatherData.getDailyForecast()) {
                    if (isSameDay(cityModel, singleDay.getDate())) {
                        continue;
                    }
                    final Map<String, String> data = new HashMap<>();
                    data.put(KEY_DATE, cityModel.getFormattedWeekdayByTimestamp(singleDay.getmTime()));
                    data.put(KEY_WEATHER, singleDay.getmWeatherCondition());
                    data.put(KEY_HIGHEST_TEMPERATURE, singleDay.getHighestTemperature(Setting.getTemperatureType(this.getActivity())));
                    data.put(KEY_LOWEST_TEMPERATURE, singleDay.getLowestTemperature(Setting.getTemperatureType(this.getActivity())));
                    cityModel.getDailyData().add(data);
                }
                dailyAdapter.notifyDataSetChanged();
            }
        }

        private boolean isSameDay(CityModel cityModel, Date date) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String cityDate = simpleDateFormat.format(cityModel.getCurrentDate());
            String otherDate = simpleDateFormat.format(date);
            return cityDate.equals(otherDate);
        }

        public void updateUICurrentTime(final CityModel cityModel) {
            final TextView date = this.getView().findViewById(R.id.single_city_date);
            date.setText(cityModel.getFormattedShortDate());
        }
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }



}
