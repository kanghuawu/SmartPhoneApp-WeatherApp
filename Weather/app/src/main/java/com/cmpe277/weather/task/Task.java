package com.cmpe277.weather.task;


import com.cmpe277.weather.CityController;

public abstract class Task {

    protected CityController controller;
    protected TaskType taskType;

    public Task(CityController controller, TaskType taskType) {
        this.controller = controller;
        this.taskType = taskType;
    }

    abstract public void execute();

}
