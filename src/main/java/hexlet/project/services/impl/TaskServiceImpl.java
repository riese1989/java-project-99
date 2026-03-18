package hexlet.project.services.impl;

import hexlet.project.dtos.requests.FilterRequestDto;
import hexlet.project.dtos.requests.TaskRequestDto;
import hexlet.project.dtos.response.TaskResponseDto;
import hexlet.project.mappers.TaskMapper;
import hexlet.project.models.Task;
import hexlet.project.repositories.TaskRepository;
import hexlet.project.services.AbstractCrudService;
import hexlet.project.services.TaskService;
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
