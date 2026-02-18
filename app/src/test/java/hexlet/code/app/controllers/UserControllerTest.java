package hexlet.code.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    private static final String BASE_URL = "/api/users";

    @Test
    @DisplayName("При получении пользователя произошла ошибка")
    void getUserErrorTest() throws Exception {
        when(userService.findById(1L)).thenThrow(new RuntimeException("User 1 not found"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешно получаем пользователя")
    void getUserSuccessTest() throws Exception {
        var userDto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .email("john.doe@example.com")
                .createdAt(LocalDateTime.now())
                .build();


        when(userService.findById(1L)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.firstName").value("John"),
                        jsonPath("$.lastName").value("Doe"),
                        jsonPath("$.email").value("john.doe@example.com"),
                        jsonPath("$.password").value("password"),
                        jsonPath("$.createdAt").exists(),
                        jsonPath("$.updatedAt").doesNotExist());
    }

    @Test
    @DisplayName("Получаем всех пользователей")
    void getAllUsersTest() throws Exception {
        var userDto1 = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .email("john.doe@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        var userDto2 = UserDto.builder()
                .id(2L)
                .firstName("John2")
                .lastName("Doe2")
                .password("password2")
                .email("john2.doe@example.com")
                .createdAt(LocalDateTime.now())
                .build();


        when(userService.findAll()).thenReturn(List.of(userDto1, userDto2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$[0].id").exists(),
                        jsonPath("$[0].firstName").value("John"),
                        jsonPath("$[0].lastName").value("Doe"),
                        jsonPath("$[0].email").value("john.doe@example.com"),
                        jsonPath("$[0].password").value("password"),
                        jsonPath("$[0].createdAt").exists(),
                        jsonPath("$[0].updatedAt").doesNotExist(),
                        jsonPath("$[1].id").exists(),
                        jsonPath("$[1].firstName").value("John2"),
                        jsonPath("$[1].lastName").value("Doe2"),
                        jsonPath("$[1].email").value("john2.doe@example.com"),
                        jsonPath("$[1].password").value("password2"),
                        jsonPath("$[1].createdAt").exists(),
                        jsonPath("$[1].updatedAt").doesNotExist()
                );
    }

    @Test
    @DisplayName("Ошибка при получении всех пользователей")
    void getAllUsersErrorTest() throws Exception {
        when(userService.findAll()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Создаём пользователя")
    void createUserTest() throws Exception {
        var userDto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .email("john.doe@example.com")
                .createdAt(LocalDateTime.now())
                .build();


        when(userService.create(userDto)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpectAll(status().isCreated(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.firstName").value("John"),
                        jsonPath("$.lastName").value("Doe"),
                        jsonPath("$.email").value("john.doe@example.com"),
                        jsonPath("$.password").value("password"),
                        jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Ошибка при создании пользователя")
    void createUserErrorTest() throws Exception {
        var userDto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .email("john.doe@example.com")
                .createdAt(LocalDateTime.now())
                .build();


        when(userService.create(userDto)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновляем пользователя")
    void updateUserTest() throws Exception {
        var updatedUserDto = UserDto.builder()
                .password("password")
                .email("john.doe@example.com")
                .build();

        var userDto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .email("john.doe@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        when(userService.update(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedUserDto)))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.firstName").value("John"),
                        jsonPath("$.lastName").value("Doe"),
                        jsonPath("$.email").value("john.doe@example.com"),
                        jsonPath("$.password").value("password"),
                        jsonPath("$.createdAt").exists(),
                        jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("При обновлении пользователя произошла ошибка")
    void updateUserErrorTest() throws Exception {
        var updatedUserDto = UserDto.builder()
                .password("password")
                .email("john.doe@example.com")
                .build();

        when(userService.update(any(UserDto.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedUserDto)))
                .andExpect(status().isBadRequest());
    }
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}