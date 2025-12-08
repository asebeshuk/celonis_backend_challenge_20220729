package com.celonis.challenge.services;

import com.celonis.challenge.events.TaskExecutionEvent;
import com.celonis.challenge.events.TaskFailedEvent;
import com.celonis.challenge.events.TaskFinishedEvent;
import com.celonis.challenge.events.TaskUpdatedEvent;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.Status;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final ApplicationEventPublisher eventPublisher;

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
        task.status = Status.IN_PROGRESS;
        taskRepository.save(task);
        eventPublisher.publishEvent(new TaskExecutionEvent(task));
    }

    @EventListener
    public void handleTaskUpdatedEvent(TaskUpdatedEvent event) {
        taskRepository.save(event.getTask());
    }

    @EventListener
    public void handleTaskFinishedEvent(TaskFinishedEvent event) {
        var task = event.getTask();
        task.status = Status.COMPLETED;
        taskRepository.save(task);
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @EventListener
    public void handleTaskFailedEvent(TaskFailedEvent event) {
        var task = event.getTask();
        task.status = Status.FAILED;
        taskRepository.save(task);
    }

    private Task get(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(NotFoundException::new);
    }
}
