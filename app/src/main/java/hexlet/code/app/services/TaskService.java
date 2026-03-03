package hexlet.code.app.services;

import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.models.Task;
import hexlet.code.app.repositories.TaskRepository;
import hexlet.code.app.utils.TaskConverter;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends AbstractCrudService<TaskDto, Task> {

    public TaskService(TaskRepository taskRepository, TaskConverter taskConverter) {
        super(taskRepository, taskConverter);
    }

    //todo переопределить, чтоб подставлялись реальные значения

    @Override
    public TaskDto create(TaskDto dto) {
        return super.create(dto);
    }

    @Override
    public String getErrorMessage() {
        return "Задача с id %s не найдена";
    }
}
