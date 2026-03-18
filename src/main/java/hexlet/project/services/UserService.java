package hexlet.project.services;

import hexlet.project.dtos.requests.UserRequestDto;
import hexlet.project.dtos.response.UserResponseDto;
import hexlet.project.models.User;

import java.util.List;

public interface UserService {
    UserResponseDto findById(Long id);
    List<UserResponseDto> findAll();
    UserResponseDto create(UserRequestDto requestDto);
    UserResponseDto update(UserRequestDto requestDto);
    void delete(Long id);
    User convertToEntity(UserRequestDto requestDto);
}
