package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.TaskStatusRequestDto;
import hexlet.code.app.dtos.response.TaskStatusResponseDto;
import hexlet.code.app.models.TaskStatus;

import java.util.List;

public interface TaskStatusService {
    TaskStatusResponseDto findById(Long id);
    List<TaskStatusResponseDto> findAll();
    TaskStatusResponseDto create(TaskStatusRequestDto requestDto);
    TaskStatusResponseDto update(TaskStatusRequestDto requestDto);
    void delete(Long id);
    TaskStatus findBySlug(String slug);
}
