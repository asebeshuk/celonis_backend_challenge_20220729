package com.celonis.challenge.events;

import com.celonis.challenge.model.Task;

public class TaskUpdatedEvent {
    private final Task task;

    public TaskUpdatedEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
