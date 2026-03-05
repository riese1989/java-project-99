package hexlet.code.app.controllers;

import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.response.TaskResponseDto;
import hexlet.code.app.services.TaskService;
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
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable final Long id) {
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
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
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
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody TaskRequestDto taskRequestDto) {
        try {
            var createdTask = taskService.create(taskRequestDto);

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
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable final Long id, @RequestBody TaskRequestDto taskRequestDto) {
        try {
            taskRequestDto.setId(id);

            var updatedUser = taskService.update(taskRequestDto);

            return new ResponseEntity<>(updatedUser, OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping
//    public ResponseEntity<List<TaskDto>> getTasks(FilterRequestDto filter) {
//        try {
//            var taskDtos = taskService.findAll(filter);
//
//            return ResponseEntity.ok()
//                    .header("X-Total-Count", String.valueOf(taskDtos.size()))
//                    .header("Access-Control-Expose-Headers", "X-Total-Count")
//                    .body(taskDtos);
//        } catch (Exception e) {
//            log.error("Error filtering tasks: {}", e.getMessage());
//            return ResponseEntity.badRequest().build();
//        }
//    }
}
