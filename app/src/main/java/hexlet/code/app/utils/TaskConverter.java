package hexlet.code.app.utils;

import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.models.Task;
import hexlet.code.app.models.TaskStatus;
import hexlet.code.app.models.User;
import hexlet.code.app.services.CrudService;
import org.springframework.stereotype.Service;

@Service
public class TaskConverter implements Converter<TaskDto, Task> {
    private final Converter<TaskStatusDto, TaskStatus> taskStatusConverter;
    private final Converter<UserDto, User> userConverter;
    private final CrudService<TaskStatusDto, TaskStatus> taskStatusService;
    private final CrudService<UserDto, User> userService;

    public TaskConverter(Converter<TaskStatusDto, TaskStatus> taskStatusConverter,
                         Converter<UserDto, User> userConverter, CrudService<TaskStatusDto, TaskStatus> taskStatusService,
                         CrudService<UserDto, User> userService) {
        this.taskStatusConverter = taskStatusConverter;
        this.userConverter = userConverter;
        this.taskStatusService = taskStatusService;
        this.userService = userService;
    }


    @Override
    public Task convertToEntity(TaskDto dto) {
        if (dto == null) {
            return null;
        }

        var task = new Task();

        task.setId(dto.getId());
        task.setName(dto.getName());
        task.setIndex(dto.getIndex());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(taskStatusService.findByIdEntity(dto.getTaskStatus().getId()));
        task.setAssignee(userService.findByIdEntity(dto.getAssignee().getId()));

        if(dto.getCreatedAt() != null) {
            task.setCreatedAt(dto.getCreatedAt());
        }

        return task;
    }

    @Override
    public TaskDto convertToDto(Task entity) {
        if (entity == null) {
            return null;
        }

        return TaskDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .index(entity.getIndex())
                .description(entity.getDescription())
                .taskStatus(taskStatusConverter.convertToDto(entity.getTaskStatus()))
                .assignee(userConverter.convertToDto(entity.getAssignee()))
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
