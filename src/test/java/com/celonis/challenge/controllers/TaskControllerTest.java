package com.celonis.challenge.controllers;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.Status;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.services.FileService;
import com.celonis.challenge.services.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private FileService fileService;

    @Test
    void testListTasks() throws Exception {
        Task task = new ProjectGenerationTask();
        task.id = "1";
        Mockito.when(taskService.listTasks()).thenReturn(Collections.singletonList(task));

        mockMvc.perform(get("/api/tasks/")
                        .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    void testCreateTask() throws Exception {
        Task task = new ProjectGenerationTask();
        task.id = "1";
        Mockito.when(taskService.createTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/api/tasks/")
                        .header("Celonis-Auth", "totally_secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test task\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }


    @Test
    void testGetResultNotCompleted() throws Exception {
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.id = "2";
        task.status = Status.CREATED;

        Mockito.when(taskService.getTask("2")).thenReturn(task);

        mockMvc.perform(get("/api/tasks/2/result")
                        .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testNotAuthorized() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header("Celonis-Auth", "wrong_secret"))
                .andExpect(status().isUnauthorized());
    }
}