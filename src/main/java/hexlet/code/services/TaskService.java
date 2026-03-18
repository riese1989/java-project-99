package hexlet.code.services;

import hexlet.code.dtos.requests.FilterRequestDto;
import hexlet.code.dtos.requests.TaskRequestDto;
import hexlet.code.dtos.response.TaskResponseDto;

import java.util.List;

public interface TaskService {
    TaskResponseDto findById(Long id);
    List<TaskResponseDto> findAll();
    TaskResponseDto create(TaskRequestDto requestDto);
    TaskResponseDto update(TaskRequestDto requestDto);
    void delete(Long id);
    List<TaskResponseDto> findByFilter(FilterRequestDto filter);
}
