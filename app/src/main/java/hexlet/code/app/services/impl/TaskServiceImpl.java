package hexlet.code.app.services.impl;

import hexlet.code.app.dtos.requests.FilterRequestDto;
import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.response.TaskResponseDto;
import hexlet.code.app.mappers.TaskMapper;
import hexlet.code.app.models.Task;
import hexlet.code.app.repositories.TaskRepository;
import hexlet.code.app.services.AbstractCrudService;
import hexlet.code.app.services.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl extends AbstractCrudService<TaskRequestDto, TaskResponseDto, Task>
    implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskStatusServiceImpl taskStatusService,
                           UserServiceImpl userService, LabelServiceImpl labelService, TaskMapper taskMapper) {
        super(taskRepository, taskMapper);
        this.taskRepository = taskRepository;
    }

    @Override
    public String getErrorMessage() {
        return "Задача с id %s не найдена";
    }

    @Override
    public List<TaskResponseDto> findByFilter(FilterRequestDto filter) {
        var tasks = taskRepository
                .findByFilter(filter.getTitleCont(), filter.getAssigneeId(), filter.getSlug(), filter.getLabelId());

        return tasks.stream().map(this::convertToResponseDto).toList();
    }
}
