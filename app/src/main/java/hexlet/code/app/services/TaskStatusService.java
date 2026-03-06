package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.TaskStatusRequestDto;
import hexlet.code.app.dtos.response.TaskStatusResponseDto;
import hexlet.code.app.models.TaskStatus;
import hexlet.code.app.repositories.TaskStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskStatusService extends AbstractCrudService<TaskStatusRequestDto, TaskStatusResponseDto, TaskStatus>
        implements CommandLineRunner {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        super(taskStatusRepository);
        this.taskStatusRepository = taskStatusRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        createDefaultStatuses("Черновик", "draft",
                "На проверке", "to_review",
                "Требует исправления", "to_be_fixed",
                "Опубликовать", "to_publish",
                "Опубликован", "published");
    }

    private void createDefaultStatuses(String... data) {
        for (int i = 0; i < data.length; i += 2) {
            var name = data[i];
            var slug = data[i + 1];
            var taskStatusDto = TaskStatusRequestDto.builder().name(name).slug(slug).build();

            create(taskStatusDto);

            log.info("Статус {} с slug {} успешно создан", name, slug);
        }
    }

    @Override
    public TaskStatus convertToEntity(TaskStatusRequestDto dto) {
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
    public TaskStatusResponseDto convertToResponseDto(TaskStatus entity) {
        if (entity == null) {
            return null;
        }

        return TaskStatusResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public void updateEntity(TaskStatusRequestDto dto, TaskStatus entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getSlug() != null) {
            entity.setSlug(dto.getSlug());
        }
    }

    @Override
    public String getErrorMessage() {
        return "Статус с id %s не найден";
    }
}
