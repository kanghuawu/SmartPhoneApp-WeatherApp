package com.cmpe277.weather;

import com.cmpe277.weather.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class CityController {

    private List<Task> taskList;
    private int current = -1;
    private CityListActivity cityListActivity;
    private CitySwipeViewActivity.SingleCityFragment singleCityFragment;

    private CityModel cityModel;

    public CityController(CityModel cityModel, CityListActivity cityListActivity) {
        this.cityModel = cityModel;
        this.cityListActivity = cityListActivity;
        taskList = new ArrayList<>();
    }

    public CityController(CityModel cityModel, CitySwipeViewActivity.SingleCityFragment singleCityFragment) {
        this.cityModel = cityModel;
        this.singleCityFragment = singleCityFragment;
        taskList = new ArrayList<>();
    }

    public void executeNext() {
        if (this.hasNext()) {
            this.nextTask().execute();
        }
    }

    public void addTask(Task task) {
        this.taskList.add(task);
    }

    public boolean hasNext() {
        return taskList.size()-1 >= current + 1;
    }

    public Task nextTask() {
        current++;
        return taskList.get(current);
    }

    public CityModel getCityModel() {
        return cityModel;
    }

    public CityListActivity getCityListActivity() {
        return cityListActivity;
    }

    public CitySwipeViewActivity.SingleCityFragment getSingleCityFragment() {
        return singleCityFragment;
    }

    public void updateTimezone(final CitySwipeViewActivity.SingleCityFragment fragment, final String timeZoneId) {
        cityModel.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        fragment.updateUICurrentTime(getCityModel());
    }

    public void updateTimezone(final CityListActivity cityListActivity, final String timeZoneId) {
        cityModel.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        cityListActivity.updateUITime(getCityModel());
    }
}
