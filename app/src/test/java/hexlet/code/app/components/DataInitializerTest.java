package hexlet.code.app;

import hexlet.code.app.components.DataInitializer;
import hexlet.code.app.models.User;
import hexlet.code.app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DataInitializerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Создаём админа")
    void testAdminIsCreated() throws Exception {
        dataInitializer.run();

        var users = userRepository.findUsersByEmail("hexlet@example.com");

        assertThat(users).isNotEmpty();
        assertEquals( 1, users.size());

        assertThat(users.get(0).getEmail()).isEqualTo("hexlet@example.com");
    }

    @Test
    @DisplayName("Не создаём админа, тк. он уже существует")
    void testNoDuplicateAdminsCreated() throws Exception {
        var user = new User();

        user.setEmail("hexlet@example.com");
        user.setPassword("123456");

        userRepository.save(user);
        dataInitializer.run();

        var users = userRepository.findUsersByEmail("hexlet@example.com");

        assertThat(users).isNotEmpty();
        assertEquals( 1, users.size());

        assertThat(users.get(0).getEmail()).isEqualTo("hexlet@example.com");

    }
}
