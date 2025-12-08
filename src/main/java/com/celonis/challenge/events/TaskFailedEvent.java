package com.celonis.challenge.events;

public class TaskFailedEvent {
    private final String taskId;

    public TaskFailedEvent(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
