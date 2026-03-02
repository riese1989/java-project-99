package hexlet.code.app.services;

import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.models.Task;
import hexlet.code.app.models.TaskStatus;
import hexlet.code.app.models.User;
import hexlet.code.app.repositories.TaskRepository;
import hexlet.code.app.utils.Converter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService implements CrudService<TaskDto, Task> {
    private final TaskRepository taskRepository;
    private final Converter<TaskDto, Task> taskConverter;
    private final Converter<TaskStatusDto, TaskStatus> taskStatusConverter;
    private final Converter<UserDto, User> userConverter;

    public TaskService(TaskRepository taskRepository, Converter<TaskDto, Task> taskConverter, Converter<TaskStatusDto,
            TaskStatus> taskStatusConverter, Converter<UserDto, User> userConverter) {
        this.taskRepository = taskRepository;
        this.taskConverter = taskConverter;
        this.taskStatusConverter = taskStatusConverter;
        this.userConverter = userConverter;
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

        if (dto.getName() != null) {
            task.setName(dto.getName());
        }

        if (dto.getIndex() != null) {
            task.setIndex(dto.getIndex());
        }

        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }

        if (dto.getTaskStatus() != null) {
            task.setTaskStatus(taskStatusConverter.convertToEntity(dto.getTaskStatus()));
        }

        if (dto.getAssignee() != null) {
            task.setAssignee(userConverter.convertToEntity(dto.getAssignee()));
        }

        return taskConverter.convertToDto(taskRepository.save(task));
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
