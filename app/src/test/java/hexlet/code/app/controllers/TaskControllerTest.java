package hexlet.code.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.dtos.UserDto;
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
        var taskDto = TaskDto.builder()
                .id(1L)
                .name("name")
                .index(2)
                .description("description")
                .taskStatus(TaskStatusDto.builder().id(3L).slug("slug").build())
                .assignee(UserDto.builder().id(4L).build())
                .build();

        when(taskService.findById(1L)).thenReturn(taskDto);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("name"),
                        jsonPath("$.index").value(2),
                        jsonPath("$.description").value("description"),
                        jsonPath("$.assignee_id").value(4),
                        jsonPath("$.status").value("slug")
                );
    }


    @Test
    @DisplayName("При получении задачи произошла ошибка")
    void getTasksError() throws Exception {
        when(taskService.findAll()).thenThrow(new RuntimeException("Произошла ошибка"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешно получены задачи")
    void getTasks() throws Exception {
        var taskDto1 = TaskDto.builder()
                .id(1L)
                .name("First Task")
                .index(10)
                .description("First Description")
                .taskStatus(TaskStatusDto.builder().slug("new").build())
                .assignee(UserDto.builder().id(100L).build())
                .build();

        var taskDto2 = TaskDto.builder()
                .id(2L)
                .name("Second Task")
                .index(20)
                .description("Second Description")
                .taskStatus(TaskStatusDto.builder().slug("done").build())
                .assignee(UserDto.builder().id(200L).build())
                .build();

        when(taskService.findAll()).thenReturn(List.of(taskDto1, taskDto2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(2),

                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].name").value("First Task"),
                        jsonPath("$[0].index").value(10),
                        jsonPath("$[0].assignee_id").value(100),
                        jsonPath("$[0].status").value("new"),

                        jsonPath("$[1].id").value(2),
                        jsonPath("$[1].name").value("Second Task"),
                        jsonPath("$[1].index").value(20),
                        jsonPath("$[1].assignee_id").value(200),
                        jsonPath("$[1].status").value("done")
                );
    }


    @Test
    @DisplayName("При добавлении задачи произошла ошибка")
    void addTaskError() throws Exception {
        var taskDto = TaskDto.builder()
                .id(1L)
                .name("First Task")
                .index(10)
                .description("First Description")
                .taskStatus(TaskStatusDto.builder()
                        .id(10L)
                        .name("New")
                        .slug("new")
                        .createdAt(LDT)
                        .build())
                .assignee(UserDto.builder()
                        .id(100L)
                        .firstName("Ivan")
                        .lastName("Ivanov")
                        .email("ivan@test.ru")
                        .password("pass1")
                        .createdAt(LDT)
                        .updatedAt(LDT)
                        .build())
                .build();

        when(taskService.create(any(TaskDto.class))).thenThrow(new RuntimeException("Произошла ошибка"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешное добавление задачи")
    void addTask() throws Exception {
        var taskDto = TaskDto.builder()
                .id(1L)
                .name("First Task")
                .index(2)
                .description("First Description")
                .taskStatus(TaskStatusDto.builder()
                        .id(3L)
                        .name("New")
                        .slug("slug")
                        .createdAt(LDT)
                        .build())
                .assignee(UserDto.builder()
                        .id(4L)
                        .firstName("Ivan")
                        .lastName("Ivanov")
                        .email("ivan@test.ru")
                        .password("pass1")
                        .createdAt(LDT)
                        .updatedAt(LDT)
                        .build())
                .build();

        when(taskService.create(any(TaskDto.class))).thenReturn(taskDto);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("First Task"),
                        jsonPath("$.index").value(2),
                        jsonPath("$.description").value("First Description"),
                        jsonPath("$.status").value("slug"),
                        jsonPath("$.assignee_id").value(4),
                        jsonPath("$.taskLabelIds").doesNotExist()
                );
    }

    @Test
    @DisplayName("При обновлении задачи произошла ошибка")
    void updateTaskError() throws Exception {
        var taskDto = TaskDto.builder()
                .id(1L)
                .name("First Task")
                .index(2)
                .description("First Description")
                .taskStatus(TaskStatusDto.builder()
                        .id(3L)
                        .name("New")
                        .slug("slug")
                        .createdAt(LDT)
                        .build())
                .assignee(UserDto.builder()
                        .id(4L)
                        .firstName("Ivan")
                        .lastName("Ivanov")
                        .email("ivan@test.ru")
                        .password("pass1")
                        .createdAt(LDT)
                        .updatedAt(LDT)
                        .build())
                .build();

        when(taskService.update(any(TaskDto.class))).thenThrow(new RuntimeException("Произошла ошибка"));

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновляем задачу")
    void updateTask() throws Exception {
        var taskDto = TaskDto.builder()
                .id(1L)
                .name("First Task")
                .index(2)
                .description("First Description")
                .taskStatus(TaskStatusDto.builder().id(3L).name("New").slug("slug").build())
                .assignee(UserDto.builder().id(4L).firstName("Ivan").build())
                .build();

        when(taskService.update(any(TaskDto.class))).thenReturn(taskDto);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("First Task"),
                        jsonPath("$.index").value(2),
                        jsonPath("$.description").value("First Description"),
                        jsonPath("$.assignee_id").value(4),
                        jsonPath("$.status").value("slug")
                );
    }


}