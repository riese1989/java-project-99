package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.response.TaskResponseDto;
import hexlet.code.app.models.Task;
import hexlet.code.app.repositories.TaskRepository;
import hexlet.code.app.utils.TaskConverter;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends AbstractCrudService<TaskRequestDto, TaskResponseDto, Task> {
    private final LabelService labelService;

    public TaskService(TaskRepository taskRepository, TaskConverter taskConverter, LabelService labelService) {
        super(taskRepository, taskConverter);
        this.labelService = labelService;
    }

    @Override
    public TaskResponseDto create(TaskRequestDto dto) {
        labelService.findOrCreate(dto.getLabels());

        return super.create(dto);
    }

    @Override
    public TaskResponseDto update(TaskRequestDto dto) {
        labelService.findOrCreate(dto.getLabels());

        return super.update(dto);
    }


    @Override
    public String getErrorMessage() {
        return "Задача с id %s не найдена";
    }
}
