package hexlet.code.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final String BASE_URL = "/api/users";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Создаём пользователя с незаполненным полем email")
    void createUserWithEmptyEmailTest() throws Exception {
        var userDto = new UserDto();

        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$[0].id").doesNotExist());
    }

    @Test
    @DisplayName("Пытаемся у пользователя укоротить password")
    void updateUserWithShortenPasswordTest() throws Exception {
        var userDto = new UserDto();

        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("12345");

        var response = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpectAll(status().isCreated())
                .andReturn();

        var createdUser = mapper.readValue(response.getResponse().getContentAsString(), UserDto.class);
        var updatedDto = new UserDto();

        updatedDto.setPassword("12");

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedDto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Пытаемся у пользователя стереть password")
    void updateUserWithEmptyPasswordTest() throws Exception {
        var userDto = new UserDto();

        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("12345");

        var response = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpectAll(status().isCreated())
                .andReturn();

        var createdUser = mapper.readValue(response.getResponse().getContentAsString(), UserDto.class);
        var updatedDto = new UserDto();

        updatedDto.setPassword("");

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedDto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Пытаемся получить пользователя, которого нет")
    void getUserNotFoundTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Пытаемся обновить данные пользователя, которого нет")
    void updateUserNotFoundTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1000000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Создаём пользователя и проверяем, что он создался")
    void createUserTest() throws Exception {
        var userDto = new UserDto();

        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpectAll(status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.firstName").value("John"),
                        jsonPath("$.lastName").value("Doe"),
                        jsonPath("$.email").value("john.doe@example.com"),
                        jsonPath("$.password").value("password"),
                        jsonPath("$.createdAt").exists(),
                        jsonPath("$.updatedAt").doesNotExist());

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$[0].id").exists(),
                        jsonPath("$[0].firstName").value("John"),
                        jsonPath("$[0].lastName").value("Doe"),
                        jsonPath("$[0].email").value("john.doe@example.com"),
                        jsonPath("$[0].password").value("password"),
                        jsonPath("$[0].createdAt").exists(),
                        jsonPath("$[0].updatedAt").exists()
                );
    }

    @Test
    @DisplayName("Создаём двух пользователей, одного из них удаляем")
    void deleteUserTest() throws Exception {
        var userDto = new UserDto();

        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("password");

        var userDto2 = new UserDto();

        userDto2.setFirstName("Ivan");
        userDto2.setLastName("Ivanov");
        userDto2.setEmail("ivan.ivanov@google.com");
        userDto2.setPassword("1234");

        var response = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();
        var response2 = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto2)))
                .andExpect(status().isCreated())
                .andReturn();
        var createdUser = mapper.readValue(response.getResponse().getContentAsString(), UserDto.class);
        var createdUser2 = mapper.readValue(response2.getResponse().getContentAsString(), UserDto.class);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + createdUser.getId()));
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + createdUser2.getId()))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.firstName").value("Ivan"),
                        jsonPath("$.lastName").value("Ivanov"),
                        jsonPath("$.email").value("ivan.ivanov@google.com"),
                        jsonPath("$.password").value("1234"),
                        jsonPath("$.createdAt").exists(),
                        jsonPath("$.updatedAt").exists());

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + createdUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Создаём пользователя и меняем данные")
    void updateUserTest() throws Exception {
        var userDto = new UserDto();

        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("password");

        var updatedData = new UserDto();

        userDto.setFirstName("Ivan");
        userDto.setLastName("Ivanov");

        var response = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();

        var createdUser = mapper.readValue(response.getResponse().getContentAsString(), UserDto.class);
        var userId = createdUser.getId();

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedData)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + userId))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.firstName").value("Ivan"),
                        jsonPath("$.lastName").value("Ivanov"),
                        jsonPath("$.email").value("john.doe@example.com"),
                        jsonPath("$.password").value("password"),
                        jsonPath("$.createdAt").exists(),
                        jsonPath("$.updatedAt").exists());
    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}