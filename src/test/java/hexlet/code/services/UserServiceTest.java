package hexlet.code.services;

import hexlet.code.dtos.requests.TaskRequestDto;
import hexlet.code.dtos.requests.TaskStatusRequestDto;
import hexlet.code.dtos.requests.UserRequestDto;
import hexlet.code.dtos.response.UserResponseDto;
import hexlet.code.repositories.TaskStatusRepository;
import hexlet.code.repositories.UserRepository;
import hexlet.code.services.impl.TaskServiceImpl;
import hexlet.code.services.impl.TaskStatusServiceImpl;
import hexlet.code.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private TaskServiceImpl taskService;
    @Autowired
    private TaskStatusServiceImpl taskStatusService;
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFields")
    @DisplayName("Создаём пользователя с незаполненным полем email")
    void createUserWithEmptyEmailTest(String description, UserRequestDto userRequestDto, String expectedMessage) {
        var ex = assertThrows(RuntimeException.class, () -> userService.create(userRequestDto));

        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    private static Stream<Arguments> provideInvalidFields() {
        return Stream.of(
                Arguments.of("Пустой email",
                        UserRequestDto.builder().firstName("John").lastName("Doe").password("password").build(),
                        "Поле email должно быть заполненным"),
                Arguments.of("Некорректный формат email",
                        UserRequestDto.builder().firstName("John").lastName("Doe").password("password").email("email").build(),
                        "Некорректный формат email"),
                Arguments.of("Пустой password",
                        UserRequestDto.builder().firstName("John").lastName("Doe").email("1@ya.ru").build(),
                        "Поле password должно быть заполненным")

        );
    }

    @Test
    @DisplayName("Создаём пользователя и сразу пытаемся его получить")
    void createUserAndGetUserTest() {
        var userDto = UserRequestDto.builder().firstName("John").lastName("Doe").password("password").email("1@ya.ru").build();

        var createdUserDto = userService.create(userDto);

        assertNotNull(createdUserDto);

        var user = userService.findById(createdUserDto.getId());

        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("1@ya.ru", user.getEmail());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Создаём пользователя только с обязательными полями")
    void createUserWithOnlyRequiredFieldsTest() {
        var userDto = UserRequestDto.builder().password("password").email("1@ya.ru").build();
        var user = userService.create(userDto);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertEquals("1@ya.ru", user.getEmail());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Пытаемся получить пользователя с некорректным id")
    void getUserWithInvalidIdTest() {
        var userDto = UserRequestDto.builder().firstName("John").lastName("Doe").password("password").email("1@ya.ru").build();

        userService.create(userDto);

        var ex = assertThrows(RuntimeException.class, () -> userService.findById(1000000L));

        assertEquals("Пользователь с id 1000000 не найден", ex.getMessage());
    }

    @Test
    @DisplayName("Пытаемся получить пользователя по email и паролю")
    void getUserByEmailAndPasswordTest() {
        var userDto = UserRequestDto.builder().firstName("John").lastName("Doe").password("password").email("1@ya.ru").build();

        userService.create(userDto);

        var foundUser = userService.findByEmailAndPassword("1@ya.ru", "password");

        assertNotNull(foundUser);
        assertNotNull(foundUser.getId());
        assertEquals("John", foundUser.getFirstName());
        assertEquals("Doe", foundUser.getLastName());
        assertEquals("1@ya.ru", foundUser.getEmail());
        assertNotNull(foundUser.getCreatedAt());
        assertNotNull(foundUser.getUpdatedAt());
    }

    @ParameterizedTest
    @DisplayName("Пытаемся получить пользователя по email и паролю, которого нет")
    @CsvSource({
            "3@ya.ru, password",
            "2@ya.ru, password2"
    })
    void getUserByEmailAndPasswordNotFoundTest(String email, String password) {
        var userDto = UserRequestDto.builder().firstName("John").lastName("Doe").password("password").email("1@ya.ru").build();

        userService.create(userDto);

        var ex = assertThrows(RuntimeException.class, () -> userService.findByEmailAndPassword(email, password));

        assertTrue(ex.getMessage().contains("Пользователь %s не найден".formatted(email)));
    }

    @Test
    @DisplayName("При пустой базе данных пытаемся получить всех пользователей")
    void getAllUsersEmptyTest() {
        assertEquals(0, userService.findAll().size());
    }

    @Test
    @DisplayName("Получаем всех пользователей")
    void getAllUsersTest() {
        var userDto1 = UserRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .email("john.doe@example.com")
                .build();

        var userDto2 = UserRequestDto.builder()
                .firstName("John2")
                .lastName("Doe2")
                .password("password2")
                .email("john2.doe@example.com")
                .build();

        userService.create(userDto1);
        userService.create(userDto2);

        var users = userService.findAll();

        assertNotNull(users);

        assertThat(users)
                .hasSize(2)
                .extracting(UserResponseDto::getEmail, UserResponseDto::getFirstName, UserResponseDto::getLastName)
                .containsExactlyInAnyOrder(
                        tuple("john.doe@example.com", "John", "Doe"),
                        tuple("john2.doe@example.com", "John2", "Doe2")
                );

        assertThat(users).allSatisfy(user -> {
            assertThat(user.getId()).isNotNull();
            assertThat(user.getCreatedAt()).isNotNull();
        });
    }

    @Test
    @DisplayName("У пользователя со всеми полями оставляем только email и password")
    void updateUserWithAllFieldsTest() {
        var userDto = UserRequestDto.builder().password("password").email("1@ya.ru").build();
        var createdUserDto = userService.create(userDto);
        var updatedDataDto = UserRequestDto.builder().id(createdUserDto.getId()).email("1@ya.ru").password("password").build();
        var updatedUserDto = userService.update(updatedDataDto);

        assertNotNull(updatedUserDto);
        assertNotNull(updatedUserDto.getId());
        assertNull(updatedUserDto.getFirstName());
        assertNull(updatedUserDto.getLastName());
        assertEquals("1@ya.ru", updatedUserDto.getEmail());
        assertNotNull(updatedUserDto.getCreatedAt());
        assertEquals(createdUserDto.getCreatedAt(), updatedUserDto.getCreatedAt());
        assertNotNull(updatedUserDto.getUpdatedAt());
    }

    @Test
    @DisplayName("Пользователю с email и password добавляем все поля")
    void updateUserWithRequiredFieldsTest2() {
        var userDto = UserRequestDto.builder().password("password").email("1@ya.ru").build();
        var createdUserDto = userService.create(userDto);
        var updatedDataDto = UserRequestDto.builder().id(createdUserDto.getId()).email("1@ya.ru").password("password").build();
        var updatedUserDto = userService.update(updatedDataDto);

        assertNotNull(updatedUserDto);
        assertNotNull(updatedUserDto.getId());
        assertNull(updatedUserDto.getFirstName());
        assertNull(updatedUserDto.getLastName());
        assertEquals("1@ya.ru", updatedUserDto.getEmail());
        assertNotNull(updatedUserDto.getCreatedAt());
        assertEquals(createdUserDto.getCreatedAt(), updatedUserDto.getCreatedAt());
        assertNotNull(updatedUserDto.getUpdatedAt());
    }

    @Test
    @DisplayName("Пытаемся удалить пользователя, который связан с задачей")
    void deleteUserWithTaskTest() {
        var userDto = UserRequestDto.builder().password("password").email("1@ya.ru").build();
        var createdUserDto = userService.create(userDto);
        var taskStatusDto = taskStatusService.create(TaskStatusRequestDto.builder().name("name").slug("slug").build());
        var taskDto = TaskRequestDto.builder().assigneeId(createdUserDto.getId())
                .title("name").slug(taskStatusDto.getSlug()).content("description").build();

        taskService.create(taskDto);

        assertThrows(RuntimeException.class, () -> userService.delete(createdUserDto.getId()));
    }
}