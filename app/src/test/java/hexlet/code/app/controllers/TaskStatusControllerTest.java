package hexlet.code.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dtos.requests.TaskStatusRequestDto;
import hexlet.code.app.dtos.response.TaskStatusResponseDto;
import hexlet.code.app.services.TaskStatusService;
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
public class TaskStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TaskStatusService taskStatusService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/task_statuses";
    private static final LocalDateTime LDT = LocalDateTime.now();

    @Test
    @DisplayName("При получении статуса произрошла ошибка")
    void getTaskStatusError() throws Exception {
        when(taskStatusService.findById(any())).thenThrow(new RuntimeException("Task status 1 not found"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешно получаем статус")
    void getTaskStatusSuccess() throws Exception {
        var request = TaskStatusRequestDto.builder().id(1L).name("New").slug("slug").createdAt(LDT).build();
        var response = TaskStatusResponseDto.builder().id(1L).name("New").slug("slug").createdAt(LDT).build();

        when(taskStatusService.findById(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("New"),
                        jsonPath("$.slug").value("slug"),
                        jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Получаем все статусы")
    void getAllTaskStatuses() throws Exception {
        var response1 = TaskStatusResponseDto.builder().id(1L).name("New").slug("slug").createdAt(LDT).build();
        var response2 = TaskStatusResponseDto.builder().id(2L).name("In progress").slug("slug2").createdAt(LDT).build();

        when(taskStatusService.findAll()).thenReturn(List.of(response1, response2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].name").value("New"),
                        jsonPath("$[0].slug").value("slug"),
                        jsonPath("$[0].createdAt").exists(),
                        jsonPath("$[1].id").value(2),
                        jsonPath("$[1].name").value("In progress"),
                        jsonPath("$[1].slug").value("slug2"),
                        jsonPath("$[1].createdAt").exists());
    }

    @Test
    @DisplayName("При получении всех статусов произошла ошибка")
    void getAllTaskStatusesError() throws Exception {
        when(taskStatusService.findAll()).thenThrow(new RuntimeException("Task statuses not found"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешное создание статуса")
    void createTaskStatusSuccess() throws Exception {
        var request = TaskStatusRequestDto.builder().id(1L).name("New").slug("slug").createdAt(LDT).build();
        var response = TaskStatusResponseDto.builder().id(1L).name("New").slug("slug").createdAt(LDT).build();

        when(taskStatusService.create(request)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpectAll(status().isCreated(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("New"),
                        jsonPath("$.slug").value("slug"),
                        jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("При создании статуса произошла ошибка")
    void createTaskStatusError() throws Exception {
        var taskStatusDto = TaskStatusRequestDto.builder().id(1L).name("New").slug("slug").createdAt(LDT).build();

        when(taskStatusService.create(taskStatusDto)).thenThrow(new RuntimeException("Task status not created"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatusDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешное обновление статуса")
    void updateTaskStatusSuccess() throws Exception {
        var request = TaskStatusRequestDto.builder().id(1L).name("New").slug("slug").createdAt(LDT).build();
        var response = TaskStatusResponseDto.builder().id(1L).name("New").slug("slug").createdAt(LDT).build();

        when(taskStatusService.update(request)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("New"),
                        jsonPath("$.slug").value("slug"),
                        jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("При обновлении статуса произошла ошибка")
    void updateTaskStatusError() throws Exception {
        var taskStatusDto = TaskStatusRequestDto.builder().id(1L).name("New").slug("slug").createdAt(LDT).build();

        when(taskStatusService.update(taskStatusDto)).thenThrow(new RuntimeException("Task status not updated"));

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatusDto)))
                .andExpect(status().isBadRequest());
    }
}
