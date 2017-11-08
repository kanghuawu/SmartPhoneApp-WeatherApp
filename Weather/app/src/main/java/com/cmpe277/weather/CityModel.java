package com.cmpe277.weather;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CityModel implements Serializable {

    private static final int REFRESH_TIMEOUT_IN_SEC = 10;

    private String cityName;
    private int position;
//    private WeatherDataModel currentWeather;
    private List<Map<String, String>> hourlyData= new ArrayList<>();
    private List<Map<String, String>> dailyData= new ArrayList<>();
    private long currentTimestamp = -1;
    private String latitude, longitude;
    private TimeZone timeZone;


    public CityModel(final CitySwipeViewActivity.SingleCityFragment fragment, final String cityName, int position) {
        this.cityName = cityName;
        this.position = position;

        executeChain(fragment);
    }

    public CityModel(final CityListActivity cityListActivity, final String cityName, int position) {
        this.cityName = cityName;
        this.position = position;

        executeChain(cityListActivity);
    }

//    public CityModel(final CityListActivity cityListActivity, String latitude, String longitude) {
//        this.latitude = latitude;
//        this.longitude = longitude;
//
//        WeatherUpdater.updateCurrentWeatherForCityList(this, cityListActivity, position, WeatherUpdater.byParamsLocation(latitude, longitude));
//    }

    public void executeChain(final CityListActivity cityListActivity) {
        if (needToRefresh()) {
            refreshCurrentTimestamp();
            // Chain 1.A
            WeatherUpdater.updateCurrentWeatherForCityList(this, cityListActivity, position, WeatherUpdater.byParamsCityName(cityName));
        }
    }

    public void executeChain(final CitySwipeViewActivity.SingleCityFragment fragment) {
        if (needToRefresh()) {
            refreshCurrentTimestamp();
            // Chain 1.B
            WeatherUpdater.updateCurrentWeatherForSingleCity(this, fragment, position, WeatherUpdater.byParamsCityName(cityName));
        }
    }

    public void updateLocalizedTime(final CityListActivity cityList, final String lat, final String lon) {
        refreshCurrentTimestamp();
        setLatAndLon(lat, lon);
        // Chain 2.A
        LocalizedTimeUpdater.updateDateByLocationForCityList(cityList, this, LocalizedTimeUpdater.byParams(this, currentTimestamp + ""));
    }


    public void updateLocalizedTime(final CitySwipeViewActivity.SingleCityFragment fragment, final String lat, final String lon) {
        refreshCurrentTimestamp();
        setLatAndLon(lat, lon);
        // Chain 2.B
        LocalizedTimeUpdater.updateDateByLocationForCityView(fragment, this, LocalizedTimeUpdater.byParams(this, currentTimestamp + ""));
    }

    private void refreshCurrentTimestamp() {
        currentTimestamp = System.currentTimeMillis();
    }

    private boolean needToRefresh() {
        return currentTimestamp == -1 || (currentTimestamp - (System.currentTimeMillis())) > REFRESH_TIMEOUT_IN_SEC;
    }

    public Date getCurrentDate() {
        return new Date(currentTimestamp);
    }

    public List<Map<String, String>> getHourlyData() {
        return hourlyData;
    }

    public List<Map<String, String>> getDailyData() {
        return dailyData;
    }

    private void setLatAndLon(String lat, String lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getPosition() {
        return position;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void updateForecast(final CitySwipeViewActivity.SingleCityFragment fragment) {
        WeatherUpdater.updateHourlyForecast(fragment, this, WeatherUpdater.byParamsCityName(cityName));
        WeatherUpdater.updateDailyForecast(fragment, this, WeatherUpdater.byParamsCityName(cityName));
    }

    public String getFormattedTimeByTimestamp(final String timestamp) {
        final SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        sdf.setTimeZone(timeZone);
        String str = sdf.format(new Date(Long.valueOf(timestamp)));
        return str;
    }

    public String getFormattedWeekdayByTimestamp(final String timestamp) {
        final SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        sdf.setTimeZone(timeZone);
        String str = sdf.format(new Date(Long.valueOf(timestamp)));
        return str;
    }

    public String getFormattedShortDate() {
        final SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMM dd HH:mm");
        sdf.setTimeZone(timeZone);
        String str = sdf.format(new Date(Long.valueOf(currentTimestamp)));
        return str;
    }

    public void updateTimezone(final CityListActivity cityListActivity, final String timeZoneId) {
        timeZone = TimeZone.getTimeZone(timeZoneId);
        cityListActivity.updateUITime(this);
    }

    public void updateTimezone(final CitySwipeViewActivity.SingleCityFragment fragment, final String timeZoneId) {
        timeZone = TimeZone.getTimeZone(timeZoneId);
        fragment.updateUICurrentTime(this);
    }

}
