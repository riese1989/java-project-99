package hexlet.code.app.utils;

import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.requests.TaskStatusRequestDto;
import hexlet.code.app.dtos.requests.UserRequestDto;
import hexlet.code.app.dtos.response.TaskResponseDto;
import hexlet.code.app.models.Task;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class TaskConverter implements Converter<TaskRequestDto, TaskResponseDto, Task> {
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
    public Task convertToEntity(TaskRequestDto dto) {
        if (dto == null) {
            return null;
        }

        var task = new Task();

        task.setId(dto.getId());
        task.setName(dto.getTitle());
        task.setIndex(dto.getIndex());
        task.setDescription(dto.getContent());

        var taskStatusRequestDto = new TaskStatusRequestDto();

        taskStatusRequestDto.setSlug(dto.getSlug());
        task.setTaskStatus(taskStatusConverter.convertToEntity(taskStatusRequestDto));

        var userRequestDto = new UserRequestDto();

        userRequestDto.setId(dto.getAssigneeId());
        task.setAssignee(userConverter.convertToEntity(userRequestDto));
        ofNullable(dto.getLabels()).ifPresent(labels ->
                task.setLabels(labels.stream()
                        .map(labelConverter::convertToEntity).collect(Collectors.toSet())));

        return task;
    }

    @Override
    public TaskResponseDto convertToResponseDto(Task entity) {
        if (entity == null) {
            return null;
        }


        return TaskResponseDto.builder()
                .id(entity.getId())
                .title(entity.getName())
                .index(entity.getIndex())
                .content(entity.getDescription())
                .status(taskStatusConverter.convertToResponseDto(entity.getTaskStatus()).getSlug())
                .assigneeId(userConverter.convertToResponseDto(entity.getAssignee()).getId())
                .labels(entity.getLabels().stream().map(labelConverter::convertToResponseDto).collect(Collectors.toSet()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public void updateEntity(TaskRequestDto dto, Task entity) {
        if (dto.getTitle() != null) {
            entity.setName(dto.getTitle());
        }

        if (dto.getIndex() != null) {
            entity.setIndex(dto.getIndex());
        }

        if (dto.getContent() != null) {
            entity.setDescription(dto.getContent());
        }

        if (dto.getSlug() != null) {
            var taskStatusRequestDto = new TaskStatusRequestDto();

            taskStatusRequestDto.setSlug(dto.getSlug());

            entity.setTaskStatus(taskStatusConverter.convertToEntity(taskStatusRequestDto));
        }

        if (dto.getLabels() != null) {
            entity.setLabels(dto.getLabels().stream()
                    .map(labelConverter::convertToEntity).collect(Collectors.toSet()));
        }

        if (dto.getAssigneeId() != null) {
            var userRequestDto = new UserRequestDto();

            userRequestDto.setId(dto.getAssigneeId());

            entity.setAssignee(userConverter.convertToEntity(userRequestDto));
        }
    }
}
