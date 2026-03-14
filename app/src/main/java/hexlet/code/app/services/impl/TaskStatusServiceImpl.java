package hexlet.code.app.services.impl;

import hexlet.code.app.dtos.requests.TaskStatusRequestDto;
import hexlet.code.app.dtos.response.TaskStatusResponseDto;
import hexlet.code.app.mappers.TaskStatusMapper;
import hexlet.code.app.models.TaskStatus;
import hexlet.code.app.repositories.TaskStatusRepository;
import hexlet.code.app.services.AbstractCrudService;
import hexlet.code.app.services.TaskStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskStatusServiceImpl extends AbstractCrudService<TaskStatusRequestDto, TaskStatusResponseDto, TaskStatus>
        implements CommandLineRunner, TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusServiceImpl(TaskStatusRepository taskStatusRepository, TaskStatusMapper taskStatusMapper) {
        super(taskStatusRepository, taskStatusMapper);
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
    public String getErrorMessage() {
        return "Статус с id %s не найден";
    }

    @Override
    public TaskStatus findBySlug(String slug) {
        return taskStatusRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Статус не найден"));
    }
}
