package com.celonis.challenge.services;

import com.celonis.challenge.events.*;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.Status;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final ApplicationEventPublisher eventPublisher;

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository,
                       ApplicationEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task getTask(String taskId) {
        return get(taskId);
    }

    public Task update(String taskId, Task task) {
        var existing = get(taskId);
        existing.creationDate = task.creationDate;
        existing.name = task.name;
        return taskRepository.save(existing);
    }

    public void delete(String taskId) {
        taskRepository.deleteById(taskId);
    }

    public void executeTask(String taskId) {
        var task = getTask(taskId);
        log.info("Executing task id={}", task);
        task.status = Status.IN_PROGRESS;
        taskRepository.save(task);
        eventPublisher.publishEvent(new TaskExecutionEvent(task));
    }

    public void cancelTask(String taskId) {
        var task = getTask(taskId);
        if (task.status == Status.IN_PROGRESS) {
            log.info("Cancel task id={}", task);
            task.status = Status.CANCELLED;
            taskRepository.save(task);
        } else {
            log.info("Task id={} is not in progress, cannot cancel", task);
        }
    }

    @EventListener
    public void handleTaskUpdatedEvent(TaskUpdatedEvent event) {
        taskRepository.save(event.getTask());
    }

    @EventListener
    public void handleTaskFinishedEvent(TaskFinishedEvent event) {
        var task = get(event.getTaskId());
        log.info("Task {} finished successfully", task);
        task.status = Status.COMPLETED;
        task.progressPercentage = 100;
        taskRepository.save(task);
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @EventListener
    public void handleTaskFailedEvent(TaskFailedEvent event) {
        var task = get(event.getTaskId());
        log.info("Task {} failed", task);
        task.status = Status.FAILED;
        taskRepository.save(task);
    }

    @EventListener
    public void handleTaskProcessingEvent(TaskProgressEvent event) {
        var task = get(event.getTaskId());
        task.progressPercentage = event.getProgressPercentage();
        log.info("Task {} progress: {}%", task.id, task.progressPercentage);
        taskRepository.save(task);
    }

    private Task get(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(NotFoundException::new);
    }
}
