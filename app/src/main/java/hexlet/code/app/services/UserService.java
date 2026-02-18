package hexlet.code.app.services;

import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.models.User;
import hexlet.code.app.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService implements CommandLineRunner {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserDto findById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь с id %s не найден".formatted(id)));

        return convertToDto(user);
    }

    public UserDto findByEmailAndPassword(String email, String password) {
        var user = userRepository.findUsersByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Пользователь %s с указанным паролем не найден".formatted(email)));

        return convertToDto(user);
    }

    public List<UserDto> findAll() {
        var users = userRepository.findAll();

        return users.stream().map(this::convertToDto).toList();
    }

    public UserDto create(UserDto userDto) {
        var user = convertToEntity(userDto);
        var savedUser = userRepository.save(user);

        userDto.setId(savedUser.getId());
        userDto.setCreatedAt(savedUser.getCreatedAt());

        return userDto;
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
            existingUser.setPassword(userDto.getPassword());
        }

        var updatedUser = userRepository.save(existingUser);

        return convertToDto(updatedUser);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public UserDto convertToDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt()).build();
    }

    public User convertToEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        var user = new User();

        user.setId(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setCreatedAt(userDto.getCreatedAt());

        return user;
    }

    @Override
    public void run(String... args) throws Exception {
        var adminEmail = "hexlet@example.com";

        if (userRepository.findUserByEmail(adminEmail).isEmpty()) {
            var admin = new User();

            admin.setEmail(adminEmail);
            admin.setPassword("qwerty");

            userRepository.save(admin);

            log.info("Default admin user created");
        }
    }
}
