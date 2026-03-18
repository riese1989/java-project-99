package hexlet.code.controllers;

import hexlet.code.dtos.requests.FilterRequestDto;
import hexlet.code.dtos.requests.TaskRequestDto;
import hexlet.code.dtos.response.TaskResponseDto;
import hexlet.code.services.impl.TaskServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto getTaskById(@PathVariable final Long id) {
        return taskService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDto createTask(@RequestBody TaskRequestDto taskRequestDto) {
        return taskService.create(taskRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable final Long id) {
        taskService.delete(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto updateTask(@PathVariable final Long id, @RequestBody TaskRequestDto taskRequestDto) {
        taskRequestDto.setId(id);

        return taskService.update(taskRequestDto);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getTasks(FilterRequestDto filter) {
        var taskDtos = taskService.findByFilter(filter);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskDtos.size()))
                .header("Access-Control-Expose-Headers", "X-Total-Count")
                .body(taskDtos);
    }
}
