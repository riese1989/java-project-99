package hexlet.project.dtos;

import lombok.Getter;

public record  LoginRequest(@Getter String username, @Getter String password) {}

