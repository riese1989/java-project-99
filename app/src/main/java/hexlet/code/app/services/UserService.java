package hexlet.code.app.services;

import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.models.User;
import hexlet.code.app.repositories.UserRepository;
import hexlet.code.app.utils.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService implements CommandLineRunner, UserDetailsService, CrudService<UserDto, User> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Converter<UserDto, User> userConverter;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       Converter<UserDto, User> userConverter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConverter = userConverter;
    }

    public UserDto findById(Long id) {
        return userConverter.convertToDto(findByIdEntity(id));
    }

    @Override
    public User findByIdEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь с id %s не найден".formatted(id)));
    }

    public UserDto findByEmailAndPassword(String email, String password) {
        var user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь %s не найден".formatted(email)));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Неверный пароль для пользователя %s".formatted(email));
        }

        return userConverter.convertToDto(user);
    }

    public List<UserDto> findAll() {
        var users = userRepository.findAll();

        return users.stream().map(userConverter::convertToDto).toList();
    }

    public UserDto create(UserDto userDto) {
        var user = userConverter.convertToEntity(userDto);

        if (user.getRole() == null) {
            user.setRole("ROLE_USER");
        }

        var savedUser = userRepository.save(user);

        return userConverter.convertToDto(savedUser);
    }

    public UserDto update(UserDto userDto) {
        var id = userDto.getId();
        var existingUser = userRepository.findById(userDto.getId()).orElseThrow(()
                -> new RuntimeException("Пользователь с id %s не найден".formatted(id)));

        if (userDto.getFirstName() != null) {
            existingUser.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            existingUser.setLastName(userDto.getLastName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        var updatedUser = userRepository.save(existingUser);

        return userConverter.convertToDto(updatedUser);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
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
                .authorities(user.getRole())
                .build();
    }
}
