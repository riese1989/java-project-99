package hexlet.code.app.controllers;

import hexlet.code.app.dtos.TaskStatusDto;
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

@RestController
@Slf4j
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    private final CrudService<TaskStatusDto> taskStatusService;

    public TaskStatusController(CrudService<TaskStatusDto> taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDto> getTaskStatusById(@PathVariable final Long id) {
        try {
            var taskStatusDto = taskStatusService.findById(id);

            return new ResponseEntity<>(taskStatusDto, OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<TaskStatusDto>> getAllTaskStatuses() {
        try {
            var taskStatusDtos = taskStatusService.findAll();

            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(taskStatusDtos.size()))
                    .header("Access-Control-Expose-Headers", "X-Total-Count")
                    .body(taskStatusDtos);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TaskStatusDto> createTaskStatus(@Valid @RequestBody TaskStatusDto taskStatusDto) {
        try {
            var createdTaskStatus = taskStatusService.create(taskStatusDto);

            return new ResponseEntity<>(createdTaskStatus, CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public void deleteTaskStatus(@PathVariable final Long id) {
        try {
            taskStatusService.delete(id);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDto> updateTaskStatus(@PathVariable final Long id, @RequestBody TaskStatusDto taskStatusDto) {
        try {
            taskStatusDto.setId(id);

            var updatedUser = taskStatusService.update(taskStatusDto);

            return new ResponseEntity<>(updatedUser, OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
