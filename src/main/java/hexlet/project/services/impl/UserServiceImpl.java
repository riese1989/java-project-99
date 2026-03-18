package hexlet.project.services.impl;

import hexlet.project.dtos.requests.UserRequestDto;
import hexlet.project.dtos.response.UserResponseDto;
import hexlet.project.mappers.UserMapper;
import hexlet.project.models.User;
import hexlet.project.repositories.UserRepository;
import hexlet.project.services.AbstractCrudService;
import hexlet.project.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends AbstractCrudService<UserRequestDto, UserResponseDto, User>
        implements CommandLineRunner, UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        super(userRepository, userMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto findByEmailAndPassword(String email, String password) {
        var user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь %s не найден".formatted(email)));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Неверный пароль для пользователя %s".formatted(email));
        }

        return convertToResponseDto(user);
    }

    @Override
    public String getErrorMessage() {
        return "Пользователь с id %s не найден";
    }

    @Override
    public void run(String... args) {
        var adminEmail = "hexlet@example.com";
        var admin = new User();

        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("qwerty"));
        admin.setRole("ROLE_ADMIN");

        userRepository.save(admin);

        log.info("Default admin user created");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().toUpperCase())
                .build();
    }
}
