package hexlet.code.app.controllers;

import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable final Long id) {
        try {
            var user = userService.findById(id);

            return new ResponseEntity<>(user, OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            var users = userService.findAll();

            return new ResponseEntity<>(users, OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {
        try {
            var createdUser = userService.create(user);

            return new ResponseEntity<>(createdUser, CREATED);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable final Long id, @RequestBody UserDto user) {
        try {
            user.setId(id);

            var updatedUser = userService.update(user);

            return new ResponseEntity<>(updatedUser, OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable final Long id) {
        try {
            userService.delete(id);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
