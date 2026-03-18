package hexlet.code.services.impl;

import hexlet.code.dtos.requests.FilterRequestDto;
import hexlet.code.dtos.requests.TaskRequestDto;
import hexlet.code.dtos.response.TaskResponseDto;
import hexlet.code.mappers.TaskMapper;
import hexlet.code.models.Task;
import hexlet.code.repositories.TaskRepository;
import hexlet.code.services.AbstractCrudService;
import hexlet.code.services.TaskService;
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
