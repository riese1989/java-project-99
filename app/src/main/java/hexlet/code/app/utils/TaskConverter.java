package hexlet.code.app.utils;

import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.models.Task;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class TaskConverter implements Converter<TaskDto, Task> {
    private final TaskStatusConverter taskStatusConverter;
    private final UserConverter userConverter;
    private final LabelConverter labelConverter;

    public TaskConverter(TaskStatusConverter taskStatusConverter,
                         UserConverter userConverter, LabelConverter labelConverter) {
        this.taskStatusConverter = taskStatusConverter;
        this.userConverter = userConverter;
        this.labelConverter = labelConverter;
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
        ofNullable(dto.getLabels()).ifPresent(labels ->
                task.setLabels(labels.stream()
                        .map(labelConverter::convertToEntity).collect(Collectors.toSet())));

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
                .labels(entity.getLabels().stream().map(labelConverter::convertToDto).collect(Collectors.toSet()))
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

        if (dto.getLabels() != null) {
            entity.setLabels(dto.getLabels().stream()
                    .map(labelConverter::convertToEntity).collect(Collectors.toSet()));
        }

        if (dto.getAssignee() != null) {
            entity.setAssignee(userConverter.convertToEntity(dto.getAssignee()));
        }
    }
}
