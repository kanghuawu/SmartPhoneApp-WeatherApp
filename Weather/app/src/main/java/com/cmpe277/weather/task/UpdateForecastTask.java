package com.cmpe277.weather.task;


import com.cmpe277.weather.CityController;
import com.cmpe277.weather.WeatherUpdater;

public class UpdateForecastTask extends Task {

    public UpdateForecastTask(CityController controller, TaskType taskType) {
        super(controller, taskType);
    }

    @Override
    public void execute() {
        if (taskType.equals(TaskType.CITY_VIEW)) {
            WeatherUpdater.updateHourlyForecast(controller.getSingleCityFragment(),
                    controller.getCityModel(),
                    WeatherUpdater.byParamsLocation(controller.getCityModel().getLatitude(), controller.getCityModel().getLongitude()));
            WeatherUpdater.updateDailyForecast(controller.getSingleCityFragment(),
                    controller.getCityModel(),
                    WeatherUpdater.byParamsLocation(controller.getCityModel().getLatitude(), controller.getCityModel().getLongitude()));
        }
    }
}
