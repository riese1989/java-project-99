package hexlet.project.controllers;

import hexlet.project.components.JwtUtils;
import hexlet.project.dtos.LoginRequest;
import hexlet.project.services.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserServiceImpl userService;
    private final JwtUtils jwtUtils;

    public AuthController(UserServiceImpl userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            userService.findByEmailAndPassword(loginRequest.getUsername(), loginRequest.password());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(403).body("Invalid credentials");
        }

        var token = jwtUtils.generateToken(loginRequest.getUsername());

        return ResponseEntity.ok(token);
    }

}

