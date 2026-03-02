package hexlet.code.app.services;

import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.models.Task;
import hexlet.code.app.repositories.TaskRepository;
import hexlet.code.app.utils.TaskConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService implements CrudService<TaskDto, Task> {
    private final TaskRepository taskRepository;
    private final TaskConverter taskConverter;

    public TaskService(TaskRepository taskRepository, TaskConverter taskConverter) {
        this.taskRepository = taskRepository;
        this.taskConverter = taskConverter;
    }

    @Override
    public TaskDto findById(Long id) {
        return taskConverter.convertToDto(findByIdEntity(id));
    }

    @Override
    public Task findByIdEntity(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача с id %s не найдена".formatted(id)));
    }

    @Override
    public List<TaskDto> findAll() {
        var tasks = taskRepository.findAll();

        return tasks.stream().map(taskConverter::convertToDto).toList();
    }

    @Override
    public TaskDto create(TaskDto dto) {
        var task = taskConverter.convertToEntity(dto);
        var savedTask = taskRepository.save(task);

        return taskConverter.convertToDto(savedTask);
    }

    @Override
    public TaskDto update(TaskDto dto) {
        var id = dto.getId();
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача с id %s не найдена".formatted(id)));

        taskConverter.updateEntity(dto, task);

        return taskConverter.convertToDto(taskRepository.save(task));
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
