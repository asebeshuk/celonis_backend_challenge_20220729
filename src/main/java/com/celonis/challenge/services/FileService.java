package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;

@Component
public class FileService {

    // not so good design. maybe be just return file and move response entity to controller
    public ResponseEntity<FileSystemResource> getTaskResult(String storageLocation) {
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

    // we can run Async if needed
    public void storeResult(File outputFile, URL url) throws IOException {
        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }
    }
}
