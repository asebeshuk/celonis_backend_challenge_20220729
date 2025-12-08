package com.celonis.challenge.controllers;

import com.celonis.challenge.model.CounterTask;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.Status;
import com.celonis.challenge.model.TaskRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/api/tasks/test")
public class TaskTestController {
    private final TaskController taskController;
    private final TaskRepository taskRepository;

    public TaskTestController(TaskController taskController, TaskRepository taskRepository) {
        this.taskController = taskController;
        this.taskRepository = taskRepository;
    }

    @PostMapping("/create/project-generation")
    public void createProjectGenerationTasks(@RequestParam(name = "count", defaultValue = "100") int count) {
        for (int i = 0; i < count; i++) {
            var task = new ProjectGenerationTask();
            task.name = "Project Generation Task " + (i + 1);
            taskController.createTask(task);
        }
    }

    @PostMapping("/create/counter")
    public void createCounterTasks(@RequestParam(name = "count", defaultValue = "100") int count) {
        for (int i = 0; i < count; i++) {
            var task = new CounterTask();
            task.name = "Counter Task " + (i + 1);
            task.start = 1;
            task.end = (int) (Math.random() * 100) + 2;
            taskController.createTask(task);
        }
    }

    @PostMapping("/execute")
    public void executeTasks(@RequestParam(name = "count", defaultValue = "10") int count) {
        taskRepository.findByStatus(Status.CREATED).stream().limit(count).forEach(task -> {
            taskController.executeTask(task.id);
        });
    }

    @PostMapping("/cancel")
    public void cancelTasks(@RequestParam(name = "count", defaultValue = "5") int count) {
        taskRepository.findByStatus(Status.IN_PROGRESS).stream().limit(count).forEach(task -> {
            taskController.cancelTask(task.id);
        });
    }
}
