package hexlet.code.app.controllers;

import hexlet.code.app.components.JwtUtils;
import hexlet.code.app.dtos.LoginRequest;
import hexlet.code.app.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            userService.findByEmailAndPassword(loginRequest.getEmail(), loginRequest.password());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(403).body("Invalid credentials");
        }

        var token = jwtUtils.generateToken(loginRequest.getEmail());

        return ResponseEntity.ok(token);
    }

}

