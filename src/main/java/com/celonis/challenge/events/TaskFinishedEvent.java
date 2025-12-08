package com.celonis.challenge.events;

import com.celonis.challenge.model.Task;

public class TaskFinishedEvent {
    private final Task task;

    public TaskFinishedEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
