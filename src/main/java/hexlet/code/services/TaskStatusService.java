package hexlet.code.services;

import hexlet.code.dtos.requests.TaskStatusRequestDto;
import hexlet.code.dtos.response.TaskStatusResponseDto;
import hexlet.code.models.TaskStatus;

import java.util.List;

public interface TaskStatusService {
    TaskStatusResponseDto findById(Long id);
    List<TaskStatusResponseDto> findAll();
    TaskStatusResponseDto create(TaskStatusRequestDto requestDto);
    TaskStatusResponseDto update(TaskStatusRequestDto requestDto);
    void delete(Long id);
    TaskStatus findBySlug(String slug);
}
