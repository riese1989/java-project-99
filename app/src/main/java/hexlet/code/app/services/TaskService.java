package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.FilterRequestDto;
import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.response.TaskResponseDto;
import hexlet.code.app.models.Task;
import hexlet.code.app.repositories.TaskRepository;
import hexlet.code.app.utils.TaskConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService extends AbstractCrudService<TaskRequestDto, TaskResponseDto, Task> {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository, TaskConverter taskConverter) {
        super(taskRepository, taskConverter);
        this.taskRepository = taskRepository;
    }

    @Override
    public String getErrorMessage() {
        return "Задача с id %s не найдена";
    }

    public List<TaskResponseDto> findByFilter(FilterRequestDto filter) {
        var tasks = taskRepository
                .findByFilter(filter.getTitleCont(), filter.getAssigneeId(), filter.getSlug(), filter.getLabelId());

        return tasks.stream().map(converter::convertToResponseDto).toList();
    }
}
