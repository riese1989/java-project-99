package hexlet.code.app.utils;

import hexlet.code.app.dtos.requests.UserRequestDto;
import hexlet.code.app.dtos.response.UserResponseDto;
import hexlet.code.app.models.User;
import hexlet.code.app.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserConverter implements Converter<UserRequestDto, UserResponseDto, User> {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserConverter(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
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
}
