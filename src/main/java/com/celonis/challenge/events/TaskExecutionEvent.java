package com.celonis.challenge.events;

import com.celonis.challenge.model.Task;

public class TaskExecutionEvent {
    private final Task task;

    public TaskExecutionEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
