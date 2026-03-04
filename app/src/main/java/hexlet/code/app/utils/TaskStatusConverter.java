package hexlet.code.app.utils;

import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.models.TaskStatus;
import hexlet.code.app.repositories.TaskStatusRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusConverter implements Converter<TaskStatusDto, TaskStatus> {
    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusConverter(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    @Override
    public TaskStatus convertToEntity(TaskStatusDto dto) {
        if (dto == null) {
            return null;
        }

        if (dto.getSlug() != null) {
            var entity = taskStatusRepository.findBySlug(dto.getSlug()).orElse(null);

            if (entity != null) {
                return entity;
            }
        }

        var taskStatus = new TaskStatus();

        taskStatus.setId(dto.getId());
        taskStatus.setName(dto.getName());
        taskStatus.setSlug(dto.getSlug());

        if (dto.getCreatedAt() != null) {
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

    @Override
    public void updateEntity(TaskStatusDto dto, TaskStatus entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getSlug() != null) {
            entity.setSlug(dto.getSlug());
        }
    }
}
