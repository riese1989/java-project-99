package hexlet.code.app.dtos;

import lombok.Getter;

public record  LoginRequest(@Getter String username, @Getter String password) {}

