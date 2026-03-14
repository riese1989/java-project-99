package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.UserRequestDto;
import hexlet.code.app.dtos.response.UserResponseDto;
import hexlet.code.app.models.User;

import java.util.List;

public interface UserService {
    UserResponseDto findById(Long id);
    List<UserResponseDto> findAll();
    UserResponseDto create(UserRequestDto requestDto);
    UserResponseDto update(UserRequestDto requestDto);
    void delete(Long id);
    User convertToEntity(UserRequestDto requestDto);
}
