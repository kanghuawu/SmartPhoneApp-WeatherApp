package com.cmpe277.weather.task;

import com.cmpe277.weather.CityController;
import com.cmpe277.weather.WeatherUpdater;

public class UpdateCurrentWeatherTask extends Task {


    public UpdateCurrentWeatherTask(CityController controller, TaskType taskType) {
        super(controller, taskType);
    }

    @Override
    public void execute() {
        if (taskType.equals(TaskType.CITY_LIST)) {
            if (controller.getCityModel().needToRefresh()) {
                controller.getCityModel().refreshCurrentTimestamp();
                // Chain 1.A
                WeatherUpdater.updateCurrentWeatherForCityList(controller,
                        controller.getCityListActivity(),
                        controller.getCityModel().getPosition(),
                        WeatherUpdater.byParamsLocation(controller.getCityModel().getLatitude(), controller.getCityModel().getLongitude()));
            }
        } else if (taskType.equals(TaskType.CITY_VIEW)) {
            if (controller.getCityModel().needToRefresh()) {
                controller.getCityModel().refreshCurrentTimestamp();
                // Chain 1.B
                WeatherUpdater.updateCurrentWeatherForSingleCity(
                        controller,
                        controller.getSingleCityFragment(),
                        controller.getCityModel().getPosition(),
                        WeatherUpdater.byParamsLocation(controller.getCityModel().getLatitude(), controller.getCityModel().getLongitude()));
            }
        }

    }
}
