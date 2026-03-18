package hexlet.code.controllers;

import hexlet.code.dtos.requests.UserRequestDto;
import hexlet.code.dtos.response.UserResponseDto;
import hexlet.code.services.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PreAuthorize("#email == authentication.name or hasRole('ADMIN')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getUserById(@PathVariable final Long id) {
            var user = userService.findById(id);

            return user;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        var users = userService.findAll();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .header("Access-Control-Expose-Headers", "X-Total-Count")
                .body(users);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUser(@PathVariable final Long id, @RequestBody UserRequestDto user) {
        user.setId(id);

        return userService.update(user);
    }

    @PreAuthorize("#email == authentication.name or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@PathVariable final Long id) {
        userService.delete(id);
    }
}
