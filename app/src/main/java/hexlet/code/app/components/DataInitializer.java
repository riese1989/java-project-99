package hexlet.code.app.components;

import hexlet.code.app.models.User;
import hexlet.code.app.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var adminEmail = "hexlet@example.com";

        if (userRepository.findUsersByEmail(adminEmail).isEmpty()) {
            var admin = new User();

            admin.setEmail(adminEmail);
            admin.setPassword("qwerty");

            userRepository.save(admin);

            log.info("Default admin user created");
        }
    }
}
