package com.celonis.challenge.events;

public class TaskProgressEvent {
    private final String taskId;
    private final int progressPercentage;

    public TaskProgressEvent(String taskId, int progressPercentage) {
        this.taskId = taskId;
        this.progressPercentage = progressPercentage;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }
}
