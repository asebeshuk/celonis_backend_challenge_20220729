package com.celonis.challenge.services;

import com.celonis.challenge.events.TaskProgressEvent;
import com.celonis.challenge.exceptions.InternalException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class FileService {

    private final ApplicationEventPublisher eventPublisher;

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    public FileService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public ResponseEntity<FileSystemResource> getTaskResult(String storageLocation) {
        log.info("Fetching file from location: {}", storageLocation);
        File inputFile = new File(storageLocation);

        if (!inputFile.exists()) {
            throw new InternalException("File not generated yet");
        }

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");

        return new ResponseEntity<>(new FileSystemResource(inputFile), respHeaders, HttpStatus.OK);
    }

    public File createFile(String taskId) throws IOException {
        File outputFile = File.createTempFile(taskId, ".zip");
        outputFile.deleteOnExit();
        return outputFile;
    }

    public void storeResult(File outputFile, URL url, String taskId) throws IOException, InterruptedException {
        long totalSize = url.openConnection().getContentLengthLong();

        log.info("Storing result for task {} to file {} (size: {} bytes)", taskId, outputFile.getAbsolutePath(), totalSize);
        try (InputStream is = url.openStream();
             ProgressInputStream progressIs = new ProgressInputStream(is, totalSize, progress -> {
                 // try to get progress for copy operation
                 this.eventPublisher.publishEvent(new TaskProgressEvent(taskId, progress));
             });
             FileOutputStream os = new FileOutputStream(outputFile)) {

            IOUtils.copyLarge(progressIs, os);
        }
    }
}
