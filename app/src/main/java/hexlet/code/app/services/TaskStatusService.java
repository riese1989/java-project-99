package hexlet.code.app.services;

import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.models.TaskStatus;
import hexlet.code.app.repositories.TaskStatusRepository;
import hexlet.code.app.utils.TaskStatusConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskStatusService extends AbstractCrudService<TaskStatusDto, TaskStatus> implements CommandLineRunner {

    public TaskStatusService(TaskStatusRepository taskStatusRepository, TaskStatusConverter taskStatusConverter) {
        super(taskStatusRepository, taskStatusConverter);
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
            var taskStatusDto = TaskStatusDto.builder().name(name).slug(slug).build();

            create(taskStatusDto);

            log.info("Статус {} с slug {} успешно создан", name, slug);
        }
    }

    @Override
    public String getErrorMessage() {
        return "Статус с id %s не найден";
    }
}
