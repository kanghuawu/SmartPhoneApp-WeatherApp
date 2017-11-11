package com.cmpe277.weather;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CityModel implements Serializable {

    private static final int REFRESH_TIMEOUT_IN_SECOND = 10;

    private String cityName;
    private int position;
//    private WeatherDataModel currentWeather;
    private List<Map<String, String>> hourlyData= new ArrayList<>();
    private List<Map<String, String>> dailyData= new ArrayList<>();
    private long currentTimestamp = -1;
    private String latitude, longitude;
    private TimeZone timeZone;

    public CityModel(final String cityName, String latitude, String longitude, int position) {
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.position = position;
    }

    public CityModel(final String cityName, String latitude, String longitude) {
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CityModel(final String cityName, int position) {
        this.cityName = cityName;
        this.position = position;
    }

    public void refreshCurrentTimestamp() {
        currentTimestamp = System.currentTimeMillis();
    }

    public boolean needToRefresh() {
        return currentTimestamp == -1 || (currentTimestamp - (System.currentTimeMillis())) > REFRESH_TIMEOUT_IN_SECOND;
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

    public void setLatAndLon(String lat, String lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public String getCityName() {
        return cityName;
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

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public long getCurrentTimestamp() {
        return currentTimestamp;
    }

    @Override
    public String toString() {
        return cityName + ", " + latitude + ", " + longitude + ", " + position;
    }
}
