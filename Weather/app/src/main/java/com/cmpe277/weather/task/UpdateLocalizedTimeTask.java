package com.cmpe277.weather.task;


import com.cmpe277.weather.CityController;
import com.cmpe277.weather.LocalizedTimeUpdater;

public class UpdateLocalizedTimeTask extends Task {

    public UpdateLocalizedTimeTask(CityController controller, TaskType taskType) {
        super(controller, taskType);
    }

    @Override
    public void execute() {
        if (taskType.equals(TaskType.CITY_LIST)) {
            controller.getCityModel().refreshCurrentTimestamp();
            // Chain 2.A
            LocalizedTimeUpdater.updateDateByLocationForCityList(
                    controller.getCityListActivity(),
                    controller,
                    LocalizedTimeUpdater.byParams(controller.getCityModel(), controller.getCityModel().getCurrentTimestamp() + ""));
        } else if (taskType.equals(TaskType.CITY_VIEW)) {
            controller.getCityModel().refreshCurrentTimestamp();
            // Chain 2.B
            LocalizedTimeUpdater.updateDateByLocationForCityView(
                    controller.getSingleCityFragment(),
                    controller,
                    LocalizedTimeUpdater.byParams(controller.getCityModel(), controller.getCityModel().getCurrentTimestamp() + ""));
        }
    }
}
