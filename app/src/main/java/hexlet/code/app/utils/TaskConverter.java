package hexlet.code.app.utils;

import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.models.Task;
import hexlet.code.app.services.TaskStatusService;
import hexlet.code.app.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class TaskConverter implements Converter<TaskDto, Task> {
    private final TaskStatusConverter taskStatusConverter;
    private final UserConverter userConverter;
    private final TaskStatusService taskStatusService;
    private final UserService userService;

    public TaskConverter(TaskStatusConverter taskStatusConverter,
                         UserConverter userConverter, TaskStatusService taskStatusService,
                         UserService userService) {
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

    @Override
    public void updateEntity(TaskDto dto, Task entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getIndex() != null) {
            entity.setIndex(dto.getIndex());
        }

        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }

        if (dto.getTaskStatus() != null) {
            entity.setTaskStatus(taskStatusConverter.convertToEntity(dto.getTaskStatus()));
        }

        if (dto.getAssignee() != null) {
            entity.setAssignee(userConverter.convertToEntity(dto.getAssignee()));
        }
    }
}
