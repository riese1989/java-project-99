package hexlet.code.app.services;

import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.models.TaskStatus;
import hexlet.code.app.repositories.TaskStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TaskStatusService implements CommandLineRunner {
    private final TaskStatusRepository taskStatusRepository;


    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    public TaskStatusDto findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Статус с id %s не найден".formatted(id)));

        return convertToDto(taskStatus);
    }

    public List<TaskStatusDto> findAll() {
        var taskStatuses = taskStatusRepository.findAll();

        return taskStatuses.stream().map(this::convertToDto).toList();
    }

    public TaskStatusDto create(TaskStatusDto taskStatusDto) {
        var taskStatus = convertToEntity(taskStatusDto);

        return convertToDto(taskStatusRepository.save(taskStatus));
    }

    public TaskStatusDto update(TaskStatusDto taskStatusDto) {
        var id = taskStatusDto.getId();
        var existingStatus = findById(id);

        if(taskStatusDto.getName() != null) {
            existingStatus.setName(taskStatusDto.getName());
        }

        if(taskStatusDto.getSlug() != null) {
            existingStatus.setSlug(taskStatusDto.getSlug());
        }

        var updatedStatus = taskStatusRepository.save(convertToEntity(existingStatus));

        return convertToDto(updatedStatus);

    }

    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }

    private TaskStatusDto convertToDto(TaskStatus taskStatus) {
        if (taskStatus == null) {
            return null;
        }

        return TaskStatusDto.builder()
                .id(taskStatus.getId())
                .name(taskStatus.getName())
                .slug(taskStatus.getSlug())
                .createdAt(taskStatus.getCreatedAt())
                .build();
    }

    private TaskStatus convertToEntity(TaskStatusDto taskStatusDto) {
        if (taskStatusDto == null) {
            return null;
        }

        var taskStatus = new TaskStatus();

        taskStatus.setId(taskStatusDto.getId());
        taskStatus.setName(taskStatusDto.getName());
        taskStatus.setSlug(taskStatusDto.getSlug());

        return taskStatus;
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
