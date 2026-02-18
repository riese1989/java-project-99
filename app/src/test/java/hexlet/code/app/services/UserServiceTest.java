package hexlet.code.app.services;

import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFields")
    @DisplayName("Создаём пользователя с незаполненным полем email")
    void createUserWithEmptyEmailTest(String description, UserDto userDto, String expectedMessage) {
        var ex = assertThrows(RuntimeException.class, () -> userService.create(userDto));

        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    private static Stream<Arguments> provideInvalidFields() {
        return Stream.of(
                Arguments.of("Пустой email",
                        UserDto.builder().firstName("John").lastName("Doe").password("password").build(),
                        "Поле email должно быть заполненным"),
                Arguments.of("Некорректный формат email",
                        UserDto.builder().firstName("John").lastName("Doe").password("password").email("email").build(),
                        "Некорректный формат email"),
                Arguments.of("Пустой password",
                        UserDto.builder().firstName("John").lastName("Doe").email("1@ya.ru").build(),
                        "Поле password должно быть заполненным"),
                Arguments.of("Пароль слишком короткий",
                        UserDto.builder().firstName("John").lastName("Doe").password("12").email("email").build(),
                        "Пароль должен содержать минимум 3 символа")

        );
    }

    @Test
    @DisplayName("Создаём пользователя и сразу пытаемся его получить")
    void createUserAndGetUserTest() {
        var userDto = UserDto.builder().firstName("John").lastName("Doe").password("password").email("1@ya.ru").build();

        var createdUserDto = userService.create(userDto);

        assertNotNull(createdUserDto);

        var user = userService.findById(createdUserDto.getId());

        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("1@ya.ru", user.getEmail());
        assertEquals("password", user.getPassword());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Создаём пользователя, а потом пытаемся создать ещё одного с тем же email")
    void createUserWithDuplicateEmailTest() {
        var userDto = UserDto.builder().firstName("John").lastName("Doe").password("password").email("1@ya.ru").build();
        var createdUserDto = userService.create(userDto);

        assertNotNull(createdUserDto);

        var userDto2 = UserDto.builder().firstName("John2").lastName("Doe2").password("pasw2").email("1@ya.ru").build();
        var ex = assertThrows(RuntimeException.class, () -> userService.create(userDto2));

        assertTrue(ex.getMessage().contains("Нарушение уникального индекса или первичного ключа"));
    }

    @Test
    @DisplayName("Создаём пользователя только с обязательными полями")
    void createUserWithOnlyRequiredFieldsTest() {
        var userDto = UserDto.builder().password("password").email("1@ya.ru").build();
        var user = userService.create(userDto);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertEquals("1@ya.ru", user.getEmail());
        assertEquals("password", user.getPassword());
        assertNotNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Пытаемся получить пользователя с некорректным id")
    void getUserWithInvalidIdTest() {
        var userDto = UserDto.builder().firstName("John").lastName("Doe").password("password").email("1@ya.ru").build();

        userService.create(userDto);

        var ex = assertThrows(RuntimeException.class, () -> userService.findById(1000000L));

        assertEquals("Пользователь с id 1000000 не найден", ex.getMessage());
    }

    @Test
    @DisplayName("Пытаемся получить пользователя по email и паролю")
    void getUserByEmailAndPasswordTest() {
        var userDto = UserDto.builder().firstName("John").lastName("Doe").password("password").email("1@ya.ru").build();

        userService.create(userDto);

        var foundUser = userService.findByEmailAndPassword("1@ya.ru", "password");

        assertNotNull(foundUser);
        assertNotNull(foundUser.getId());
        assertEquals("John", foundUser.getFirstName());
        assertEquals("Doe", foundUser.getLastName());
        assertEquals("1@ya.ru", foundUser.getEmail());
        assertEquals("password", foundUser.getPassword());
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
        var userDto = UserDto.builder().firstName("John").lastName("Doe").password("password").email("1@ya.ru").build();

        userService.create(userDto);

        var ex = assertThrows(RuntimeException.class, () -> userService.findByEmailAndPassword(email, password));

        assertTrue(ex.getMessage().contains("Пользователь %s с указанным паролем не найден".formatted(email)));
    }

    @Test
    @DisplayName("При пустой базе данных пытаемся получить всех пользователей")
    void getAllUsersEmptyTest() {
        assertEquals(0, userService.findAll().size());
    }

    @Test
    @DisplayName("Получаем всех пользователей")
    void getAllUsersTest() {
        var userDto1 = UserDto.builder()
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .email("john.doe@example.com")
                .build();

        var userDto2 = UserDto.builder()
                .firstName("John2")
                .lastName("Doe2")
                .password("password2")
                .email("john2.doe@example.com")
                .build();

        userService.create(userDto1);
        userService.create(userDto2);

        var users = userService.findAll();

        assertNotNull(users);
        assertEquals(2, users.size());

        var createdUser1 = users.get(0);

        assertNotNull(createdUser1);
        assertNotNull(createdUser1.getId());
        assertEquals("John", createdUser1.getFirstName());
        assertEquals("Doe", createdUser1.getLastName());
        assertEquals("john.doe@example.com", createdUser1.getEmail());
        assertEquals("password", createdUser1.getPassword());
        assertNotNull(createdUser1.getCreatedAt());
        assertNotNull(createdUser1.getUpdatedAt());

        var createdUser2 = users.get(1);

        assertNotNull(createdUser2);
        assertNotNull(createdUser2.getId());
        assertEquals("John2", createdUser2.getFirstName());
        assertEquals("Doe2", createdUser2.getLastName());
        assertEquals("john2.doe@example.com", createdUser2.getEmail());
        assertEquals("password2", createdUser2.getPassword());
        assertNotNull(createdUser2.getCreatedAt());
        assertNotNull(createdUser2.getUpdatedAt());
    }

    @Test
    @DisplayName("У пользователя со всеми полями оставляем только email и password")
    void updateUserWithAllFieldsTest() {
        var userDto = UserDto.builder().firstName("John").lastName("Doe").password("password").email("1@ya.ru").build();
        var createdUserDto = userService.create(userDto);
        var updatedDataDto = UserDto.builder().id(createdUserDto.getId()).email("1@ya.ru").password("password").build();
        var updatedUserDto = userService.update(updatedDataDto);

        assertNotNull(updatedUserDto);
        assertNotNull(updatedUserDto.getId());
        assertNull(updatedUserDto.getFirstName());
        assertNull(updatedUserDto.getLastName());
        assertEquals("1@ya.ru", updatedUserDto.getEmail());
        assertEquals("password", updatedUserDto.getPassword());
        assertNotNull(updatedUserDto.getCreatedAt());
        assertEquals(createdUserDto.getCreatedAt(), updatedUserDto.getCreatedAt());
        assertNotNull(updatedUserDto.getUpdatedAt());
        assertNotEquals(createdUserDto.getUpdatedAt(), updatedUserDto.getUpdatedAt());
        assertTrue(updatedUserDto.getUpdatedAt().isAfter(createdUserDto.getUpdatedAt()));
    }

    @Test
    @DisplayName("Пользователю с email и password добавляем все поля")
    void updateUserWithRequiredFieldsTest2() {
        var userDto = UserDto.builder().password("password").email("1@ya.ru").build();
        var createdUserDto = userService.create(userDto);
        var updatedDataDto = UserDto.builder().id(createdUserDto.getId()).email("1@ya.ru").password("password").build();
        var updatedUserDto = userService.update(updatedDataDto);

        assertNotNull(updatedUserDto);
        assertNotNull(updatedUserDto.getId());
        assertNull(updatedUserDto.getFirstName());
        assertNull(updatedUserDto.getLastName());
        assertEquals("1@ya.ru", updatedUserDto.getEmail());
        assertEquals("password", updatedUserDto.getPassword());
        assertNotNull(updatedUserDto.getCreatedAt());
        assertEquals(createdUserDto.getCreatedAt(), updatedUserDto.getCreatedAt());
        assertNotNull(updatedUserDto.getUpdatedAt());
        assertNotEquals(createdUserDto.getUpdatedAt(), updatedUserDto.getUpdatedAt());
        assertTrue(updatedUserDto.getUpdatedAt().isAfter(createdUserDto.getUpdatedAt()));
    }
}