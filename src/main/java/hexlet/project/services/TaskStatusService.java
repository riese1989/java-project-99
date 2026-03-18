package hexlet.project.services;

import hexlet.project.dtos.requests.TaskStatusRequestDto;
import hexlet.project.dtos.response.TaskStatusResponseDto;
import hexlet.project.models.TaskStatus;

import java.util.List;

public interface TaskStatusService {
    TaskStatusResponseDto findById(Long id);
    List<TaskStatusResponseDto> findAll();
    TaskStatusResponseDto create(TaskStatusRequestDto requestDto);
    TaskStatusResponseDto update(TaskStatusRequestDto requestDto);
    void delete(Long id);
    TaskStatus findBySlug(String slug);
}
