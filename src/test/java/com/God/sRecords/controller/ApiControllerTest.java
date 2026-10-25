package com.God.sRecords.controller;

import com.God.sRecords.one.controller.ApiController;
import com.God.sRecords.one.model.Task;
import com.God.sRecords.one.model.TypeTask;
import com.God.sRecords.one.regestration.model.User;
import com.God.sRecords.one.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ApiControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private ApiController apiController;

    private MockMvc mockMvc;
    private Task task;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(apiController).build();

        User user = new User("Иван", "ivan@mail.com", "x");
        user.setId(1L);
        task = new Task("Заметка", "Текст", user);
        task.setId(10L);
    }

    @Test
    void create_returns201() throws Exception {
        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Заголовок\",\"body\":\"Тело\"}"))
                .andExpect(status().isCreated());

        verify(taskService).createTask(eq("Заголовок"), eq("Тело"), any());
    }

    @Test
    void update_returns200_andPassesType() throws Exception {
        mockMvc.perform(put("/api/update/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"N\",\"body\":\"B\",\"type\":\"ARCHIVED\"}"))
                .andExpect(status().isOk());

        verify(taskService).updateTask(eq(10L), eq("N"), eq("B"), eq(TypeTask.ARCHIVED), any());
    }

    @Test
    void getTask_returnsTask_whenFound() throws Exception {
        when(taskService.currentTask(eq(10L), any())).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/task/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Заметка"));
    }

    @Test
    void getTask_returns404_whenMissing() throws Exception {
        when(taskService.currentTask(eq(10L), any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/task/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByType_returnsFilteredList() throws Exception {
        when(taskService.tasksType(any(), eq(TypeTask.TRASH))).thenReturn(List.of(task));

        mockMvc.perform(get("/api/all/type").param("type", "TRASH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void deleteTrash_returns204() throws Exception {
        mockMvc.perform(delete("/api/delete"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(any());
    }
}