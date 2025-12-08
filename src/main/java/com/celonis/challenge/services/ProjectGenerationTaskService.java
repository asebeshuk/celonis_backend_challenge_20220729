package com.celonis.challenge.services;

import com.celonis.challenge.events.TaskExecutionEvent;
import com.celonis.challenge.events.TaskFailedEvent;
import com.celonis.challenge.events.TaskFinishedEvent;
import com.celonis.challenge.events.TaskUpdatedEvent;
import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.Task;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URL;

@Service
public class ProjectGenerationTaskService {

    private final FileService fileService;
    private final ApplicationEventPublisher eventPublisher;

    public ProjectGenerationTaskService(FileService fileService,
                                        ApplicationEventPublisher eventPublisher) {
        this.fileService = fileService;
        this.eventPublisher = eventPublisher;
    }

    @Async
    @EventListener
    @Transactional(dontRollbackOn = {InternalException.class})
    public void handleTaskExecutionEvent(TaskExecutionEvent event) {
        if (isProjectGenerationTask(event.getTask())) {
            ProjectGenerationTask projectGenerationTask = (ProjectGenerationTask) event.getTask();
            try {
                URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
                if (url == null) {
                    throw new InternalException("Zip file not found");
                }
                var outputFile = fileService.createFile(projectGenerationTask.id);

                projectGenerationTask.storageLocation = outputFile.getAbsolutePath();
                eventPublisher.publishEvent(new TaskUpdatedEvent(projectGenerationTask));

                fileService.storeResult(outputFile, url);

                eventPublisher.publishEvent(new TaskFinishedEvent(projectGenerationTask));
            } catch (Exception e) {
                eventPublisher.publishEvent(new TaskFailedEvent(projectGenerationTask));
                throw new InternalException(e);
            }
        }
    }

    private boolean isProjectGenerationTask(Task task) {
        return task instanceof com.celonis.challenge.model.ProjectGenerationTask;
    }
}
