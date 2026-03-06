package hexlet.code.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dtos.requests.FilterRequestDto;
import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.response.TaskResponseDto;
import hexlet.code.app.services.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TaskService taskService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/tasks";
    private static final LocalDateTime LDT = LocalDateTime.now();

    @Test
    @DisplayName("При получении задачи произошла ошибка")
    void getTaskError() throws Exception {
        when(taskService.findById(1L)).thenThrow(new RuntimeException("Задача не найдена"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешно получена задача")
    void getTask() throws Exception {
        var request = TaskRequestDto.builder()
                .id(1L)
                .title("name")
                .index(2)
                .content("description")
                .slug("slug")
                .assigneeId(4L)
                .build();
        var response = TaskResponseDto.builder()
                .id(1L)
                .title("name")
                .index(2)
                .content("description")
                .status("slug")
                .assigneeId(4L)
                .build();

        when(taskService.findById(1L)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.title").value("name"),
                        jsonPath("$.index").value(2),
                        jsonPath("$.content").value("description"),
                        jsonPath("$.assignee_id").value(4),
                        jsonPath("$.status").value("slug")
                );
    }


    @Test
    @DisplayName("При получении задачи произошла ошибка")
    void getTasksError() throws Exception {
        when(taskService.findByFilter(any(FilterRequestDto.class)))
                .thenThrow(new RuntimeException("Произошла ошибка"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешно получены задачи")
    void getTasks() throws Exception {
        var response1 = TaskResponseDto.builder()
                .id(1L)
                .title("First Task")
                .index(10)
                .content("First Description")
                .status("new")
                .assigneeId(100L)
                .build();

        var response2 = TaskResponseDto.builder()
                .id(2L)
                .title("Second Task")
                .index(20)
                .content("Second Description")
                .status("done")
                .assigneeId(200L)
                .build();

        when(taskService.findByFilter(any(FilterRequestDto.class))).thenReturn(List.of(response1, response2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(2),

                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].title").value("First Task"),
                        jsonPath("$[0].index").value(10),
                        jsonPath("$[0].assignee_id").value(100),
                        jsonPath("$[0].status").value("new"),

                        jsonPath("$[1].id").value(2),
                        jsonPath("$[1].title").value("Second Task"),
                        jsonPath("$[1].index").value(20),
                        jsonPath("$[1].assignee_id").value(200),
                        jsonPath("$[1].status").value("done")
                );
    }


    @Test
    @DisplayName("При добавлении задачи произошла ошибка")
    void addTaskError() throws Exception {
        var taskDto = TaskRequestDto.builder()
                .id(1L)
                .title("First Task")
                .index(10)
                .content("First Description")
                .slug("new")
                .assigneeId(200L)
                .build();

        when(taskService.create(any(TaskRequestDto.class))).thenThrow(new RuntimeException("Произошла ошибка"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешное добавление задачи")
    void addTask() throws Exception {
        var request = TaskRequestDto.builder()
                .id(1L)
                .title("First Task")
                .index(2)
                .content("First Description")
                .slug("slug")
                .assigneeId(4L)
                .build();

        var response = TaskResponseDto.builder()
                .id(1L)
                .title("First Task")
                .index(2)
                .content("First Description")
                .status("slug")
                .assigneeId(4L)
                .build();

        when(taskService.create(any(TaskRequestDto.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.title").value("First Task"),
                        jsonPath("$.index").value(2),
                        jsonPath("$.content").value("First Description"),
                        jsonPath("$.status").value("slug"),
                        jsonPath("$.assignee_id").value(4),
                        jsonPath("$.taskLabelIds").doesNotExist()
                );
    }

    @Test
    @DisplayName("При обновлении задачи произошла ошибка")
    void updateTaskError() throws Exception {
        var taskDto = TaskRequestDto.builder()
                .id(1L)
                .title("First Task")
                .index(2)
                .content("First Description")
                .slug("slug")
                .assigneeId(4L)
                .build();

        when(taskService.update(any(TaskRequestDto.class))).thenThrow(new RuntimeException("Произошла ошибка"));

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновляем задачу")
    void updateTask() throws Exception {
        var request = TaskRequestDto.builder()
                .id(1L)
                .title("First Task")
                .index(2)
                .content("First Description")
                .slug("slug")
                .assigneeId(4L)
                .build();
        var response = TaskResponseDto.builder()
                .id(1L)
                .title("First Task")
                .index(2)
                .content("First Description")
                .status("slug")
                .assigneeId(4L)
                .build();

        when(taskService.update(any(TaskRequestDto.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.title").value("First Task"),
                        jsonPath("$.index").value(2),
                        jsonPath("$.content").value("First Description"),
                        jsonPath("$.assignee_id").value(4),
                        jsonPath("$.status").value("slug")
                );
    }


}