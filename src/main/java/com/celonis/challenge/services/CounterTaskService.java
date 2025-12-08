package com.celonis.challenge.services;

import com.celonis.challenge.events.TaskFinishedEvent;
import com.celonis.challenge.events.TaskUpdatedEvent;
import com.celonis.challenge.model.CounterTaskRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CounterTaskService {

    private final CounterTaskRepository counterTaskRepository;

    private final ApplicationEventPublisher eventPublisher;

    private static final Logger log = LoggerFactory.getLogger(CounterTaskService.class);

    public CounterTaskService(CounterTaskRepository counterTaskRepository, ApplicationEventPublisher eventPublisher) {
        this.counterTaskRepository = counterTaskRepository;
        this.eventPublisher = eventPublisher;
    }

    @SchedulerLock(name = "counterTasks",
            lockAtMostFor = "PT50S", lockAtLeastFor = "PT10S")
    @Scheduled(fixedRate = 1000)
    public void performTask() {
        counterTaskRepository.findByStatus(com.celonis.challenge.model.Status.IN_PROGRESS)
                .forEach(task -> {
                    log.info("Processing CounterTask id={}", task);
                    task.pointer += 1;
                    if (task.pointer == task.end) {
                        eventPublisher.publishEvent(new TaskFinishedEvent(task.id));
                    } else {
                        task.progressPercentage = (task.pointer - task.start) * 100 / (task.end - task.start);
                        eventPublisher.publishEvent(new TaskUpdatedEvent(task));
                    }
                });
    }
}
