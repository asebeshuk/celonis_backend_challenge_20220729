package com.celonis.challenge.events;

import com.celonis.challenge.model.Task;

public class TaskFailedEvent {
    private final Task task;

    public TaskFailedEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
