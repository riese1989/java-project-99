package hexlet.code.services;

import hexlet.code.dtos.requests.UserRequestDto;
import hexlet.code.dtos.response.UserResponseDto;
import hexlet.code.models.User;

import java.util.List;

public interface UserService {
    UserResponseDto findById(Long id);
    List<UserResponseDto> findAll();
    UserResponseDto create(UserRequestDto requestDto);
    UserResponseDto update(UserRequestDto requestDto);
    void delete(Long id);
    User convertToEntity(UserRequestDto requestDto);
}
