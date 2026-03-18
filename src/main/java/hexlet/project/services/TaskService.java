package hexlet.project.services;

import hexlet.project.dtos.requests.FilterRequestDto;
import hexlet.project.dtos.requests.TaskRequestDto;
import hexlet.project.dtos.response.TaskResponseDto;

import java.util.List;

public interface TaskService {
    TaskResponseDto findById(Long id);
    List<TaskResponseDto> findAll();
    TaskResponseDto create(TaskRequestDto requestDto);
    TaskResponseDto update(TaskRequestDto requestDto);
    void delete(Long id);
    List<TaskResponseDto> findByFilter(FilterRequestDto filter);
}
