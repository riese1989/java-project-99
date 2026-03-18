package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.FilterRequestDto;
import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.response.TaskResponseDto;

import java.util.List;

public interface TaskService {
    TaskResponseDto findById(Long id);
    List<TaskResponseDto> findAll();
    TaskResponseDto create(TaskRequestDto requestDto);
    TaskResponseDto update(TaskRequestDto requestDto);
    void delete(Long id);
    List<TaskResponseDto> findByFilter(FilterRequestDto filter);
}
