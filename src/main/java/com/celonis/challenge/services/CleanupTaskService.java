package com.celonis.challenge.services;

import com.celonis.challenge.model.Status;
import com.celonis.challenge.model.TaskRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class CleanupTaskService {
    private final TaskRepository taskRepository;

    private static final Logger log = LoggerFactory.getLogger(CleanupTaskService.class);
    @Value("${celonis.task.cleanup.older-than-days:7}")
    private long olderThanDays;

    public CleanupTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @SchedulerLock(name = "cleanupTasks",
            lockAtMostFor = "PT50S", lockAtLeastFor = "PT10S")
    @Scheduled(cron = "${celonis.task.cleanup.cron:0 0 * * * *}")
    @Transactional
    public void performCleanup() {
        Date cutoff = java.util.Date.from(
                LocalDateTime.now().minusDays(olderThanDays)
                        .atZone(ZoneId.systemDefault()).toInstant()
        );

        log.info("Performing cleanup of tasks with status CREATED older than {}", cutoff);
        taskRepository.deleteByStatusAndOlderThan(Status.CREATED, cutoff);
    }
}
