package hexlet.code.app.services;

import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.models.TaskStatus;
import hexlet.code.app.repositories.TaskStatusRepository;
import hexlet.code.app.utils.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TaskStatusService implements CommandLineRunner, CrudService<TaskStatusDto> {
    private final TaskStatusRepository taskStatusRepository;
    private final Converter<TaskStatusDto, TaskStatus> taskStatusConverter;


    public TaskStatusService(TaskStatusRepository taskStatusRepository, Converter<TaskStatusDto, TaskStatus> taskStatusConverter) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskStatusConverter = taskStatusConverter;
    }

    public TaskStatusDto findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Статус с id %s не найден".formatted(id)));

        return taskStatusConverter.convertToDto(taskStatus);
    }

    public List<TaskStatusDto> findAll() {
        var taskStatuses = taskStatusRepository.findAll();

        return taskStatuses.stream().map(taskStatusConverter::convertToDto).toList();
    }

    public TaskStatusDto create(TaskStatusDto taskStatusDto) {
        var taskStatus = taskStatusConverter.convertToEntity(taskStatusDto);

        return taskStatusConverter.convertToDto(taskStatusRepository.save(taskStatus));
    }

    public TaskStatusDto update(TaskStatusDto taskStatusDto) {
        var id = taskStatusDto.getId();
        var existingStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Статус с id %s не найден".formatted(id)));

        if(taskStatusDto.getName() != null) {
            existingStatus.setName(taskStatusDto.getName());
        }

        if(taskStatusDto.getSlug() != null) {
            existingStatus.setSlug(taskStatusDto.getSlug());
        }

        var updatedStatus = taskStatusRepository.save(existingStatus);

        return taskStatusConverter.convertToDto(updatedStatus);
    }

    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
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
}
