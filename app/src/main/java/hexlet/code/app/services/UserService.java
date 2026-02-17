package hexlet.code.app.services;

import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.models.User;
import hexlet.code.app.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserDto findById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User %s not found".formatted(id)));

        return convertToDto(user);
    }

    public UserDto findByEmailAndPassword(String email, String password) {
        var user = userRepository.findUsersByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("User %s and current password not found".formatted(email)));

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
                -> new RuntimeException("User %s not found".formatted(id)));

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

        var userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());

        return userDto;
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
}
