package com.celonis.challenge.services;

import com.celonis.challenge.events.TaskExecutionEvent;
import com.celonis.challenge.events.TaskFailedEvent;
import com.celonis.challenge.events.TaskFinishedEvent;
import com.celonis.challenge.events.TaskUpdatedEvent;
import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ProjectGenerationTaskService.class);

    public ProjectGenerationTaskService(FileService fileService,
                                        ApplicationEventPublisher eventPublisher) {
        this.fileService = fileService;
        this.eventPublisher = eventPublisher;
    }

    // Async to not block the main thread
    @Async
    @EventListener
    @Transactional(dontRollbackOn = {InternalException.class})
    public void handleTaskExecutionEvent(TaskExecutionEvent event) {
        if (shouldProcess(event.getTask())) {
            ProjectGenerationTask projectGenerationTask = (ProjectGenerationTask) event.getTask();

            log.info("Processing ProjectGenerationTask id={}", projectGenerationTask);
            try {
                URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
                if (url == null) {
                    throw new InternalException("Zip file not found");
                }
                var outputFile = fileService.createFile(projectGenerationTask.id);

                projectGenerationTask.storageLocation = outputFile.getAbsolutePath();

                // we can not really cancel the copy task once started, so we skip cancellation check here
                eventPublisher.publishEvent(new TaskUpdatedEvent(projectGenerationTask));

                fileService.storeResult(outputFile, url, projectGenerationTask.id);

                eventPublisher.publishEvent(new TaskFinishedEvent(projectGenerationTask.id));
            } catch (Exception e) {
                eventPublisher.publishEvent(new TaskFailedEvent(projectGenerationTask.id));
                throw new InternalException(e);
            }
        }
    }

    private boolean shouldProcess(Task task) {
        return task instanceof com.celonis.challenge.model.ProjectGenerationTask;
    }
}
