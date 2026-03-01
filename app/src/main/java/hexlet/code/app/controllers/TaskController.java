package hexlet.code.app.controllers;

import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.services.CrudService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@RequestMapping("/api/tasks")
public class TaskController {
    private final CrudService<TaskDto> taskService;

    public TaskController(CrudService<TaskDto> taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable final Long id) {
        try {
            var taskDto = taskService.findById(id);

            return new ResponseEntity<>(taskDto, OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        try {
            var taskDtos = taskService.findAll();

            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(taskDtos.size()))
                    .header("Access-Control-Expose-Headers", "X-Total-Count")
                    .body(taskDtos);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        try {
            var createdTask = taskService.create(taskDto);

            return new ResponseEntity<>(createdTask, CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable final Long id) {
        try {
            taskService.delete(id);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable final Long id, @RequestBody TaskDto taskDto) {
        try {
            taskDto.setId(id);

            var updatedUser = taskService.update(taskDto);

            return new ResponseEntity<>(updatedUser, OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
