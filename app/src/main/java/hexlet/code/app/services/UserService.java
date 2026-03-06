package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.UserRequestDto;
import hexlet.code.app.dtos.response.UserResponseDto;
import hexlet.code.app.models.User;
import hexlet.code.app.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService extends AbstractCrudService<UserRequestDto, UserResponseDto, User>
        implements CommandLineRunner, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(userRepository);
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
    public User convertToEntity(UserRequestDto dto) {
        if (dto == null) {
            return null;
        }

        if (dto.getId() != null) {
            var entity = userRepository.findById(dto.getId()).orElse(null);

            if (entity != null) {
                return entity;
            }
        }

        var user = new User();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        var password = dto.getPassword();

        if (password != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRole() == null) {
            user.setRole("ROLE_USER");
        }

        return user;
    }

    @Override
    public UserResponseDto convertToResponseDto(User entity) {
        if (entity == null) {
            return null;
        }

        return UserResponseDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt()).build();
    }

    @Override
    public void updateEntity(UserRequestDto dto, User entity) {
        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
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
                .authorities(user.getRole())
                .build();
    }
}
