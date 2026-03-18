package hexlet.code.services.impl;

import hexlet.code.dtos.requests.UserRequestDto;
import hexlet.code.dtos.response.UserResponseDto;
import hexlet.code.mappers.UserMapper;
import hexlet.code.models.User;
import hexlet.code.repositories.UserRepository;
import hexlet.code.services.AbstractCrudService;
import hexlet.code.services.UserService;
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
                .roles(user.getRole())
                .build();
    }
}
