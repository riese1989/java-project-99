package hexlet.code.app.utils;

import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.models.TaskStatus;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusConverter implements Converter<TaskStatusDto, TaskStatus> {
    @Override
    public TaskStatus convertToEntity(TaskStatusDto dto) {
        if (dto == null) {
            return null;
        }

        var taskStatus = new TaskStatus();

        taskStatus.setId(dto.getId());
        taskStatus.setName(dto.getName());
        taskStatus.setSlug(dto.getSlug());

        if(dto.getCreatedAt() != null) {
            taskStatus.setCreatedAt(dto.getCreatedAt());
        }

        return taskStatus;
    }

    @Override
    public TaskStatusDto convertToDto(TaskStatus entity) {
        if (entity == null) {
            return null;
        }

        return TaskStatusDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
