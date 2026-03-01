package hexlet.code.app.utils;

import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.models.Task;
import hexlet.code.app.models.TaskStatus;
import hexlet.code.app.models.User;
import org.springframework.stereotype.Service;

@Service
public class TaskConverter implements Converter<TaskDto, Task> {
    private final Converter<TaskStatusDto, TaskStatus> taskStatusConverter;
    private final Converter<UserDto, User> userConverter;

    public TaskConverter(Converter<TaskStatusDto, TaskStatus> taskStatusConverter, Converter<UserDto, User> userConverter) {
        this.taskStatusConverter = taskStatusConverter;
        this.userConverter = userConverter;
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
        task.setTaskStatus(taskStatusConverter.convertToEntity(dto.getTaskStatus()));
        task.setAssignee(userConverter.convertToEntity(dto.getAssignee()));

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
