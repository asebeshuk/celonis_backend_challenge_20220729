package com.celonis.challenge.events;

public class TaskFinishedEvent {
    private final String taskId;

    public TaskFinishedEvent(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
