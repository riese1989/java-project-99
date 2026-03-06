package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.FilterRequestDto;
import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.requests.TaskStatusRequestDto;
import hexlet.code.app.dtos.requests.UserRequestDto;
import hexlet.code.app.dtos.response.TaskResponseDto;
import hexlet.code.app.models.Label;
import hexlet.code.app.models.Task;
import hexlet.code.app.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class TaskService extends AbstractCrudService<TaskRequestDto, TaskResponseDto, Task> {
    private final TaskRepository taskRepository;
    private final TaskStatusService taskStatusService;
    private final UserService userService;
    private final LabelService labelService;

    public TaskService(TaskRepository taskRepository, TaskStatusService taskStatusService,
                       UserService userService, LabelService labelService) {
        super(taskRepository);
        this.taskRepository = taskRepository;
        this.taskStatusService = taskStatusService;
        this.userService = userService;
        this.labelService = labelService;
    }

    @Override
    public Task convertToEntity(TaskRequestDto dto) {
        if (dto == null) {
            return null;
        }

        var task = new Task();

        task.setId(dto.getId());
        task.setName(dto.getTitle());
        task.setIndex(dto.getIndex());
        task.setDescription(dto.getContent());

        var taskStatusRequestDto = new TaskStatusRequestDto();

        taskStatusRequestDto.setSlug(dto.getSlug());
        task.setTaskStatus(taskStatusService.convertToEntity(taskStatusRequestDto));

        if (dto.getAssigneeId() != null) {
            var userRequestDto = new UserRequestDto();

            userRequestDto.setId(dto.getAssigneeId());
            task.setAssignee(userService.convertToEntity(userRequestDto));
        }
        task.setLabels(labelService.convertToEntities(dto.getTaskLabelIds()));

        return task;
    }

    @Override
    public TaskResponseDto convertToResponseDto(Task entity) {
        if (entity == null) {
            return null;
        }

        var dtoBuilder = TaskResponseDto.builder()
                .id(entity.getId())
                .title(entity.getName())
                .index(entity.getIndex())
                .content(entity.getDescription())
                .status(taskStatusService.convertToResponseDto(entity.getTaskStatus()).getSlug())
                .taskLabelIds(entity.getLabels().stream().map(Label::getId).collect(Collectors.toSet()))
                .createdAt(entity.getCreatedAt());

        ofNullable(entity.getAssignee()).ifPresent(a -> dtoBuilder.assigneeId(a.getId()));


        return dtoBuilder.build();
    }

    @Override
    public void updateEntity(TaskRequestDto dto, Task entity) {
        if (dto.getTitle() != null) {
            entity.setName(dto.getTitle());
        }

        if (dto.getIndex() != null) {
            entity.setIndex(dto.getIndex());
        }

        if (dto.getContent() != null) {
            entity.setDescription(dto.getContent());
        }

        if (dto.getSlug() != null) {
            var taskStatusRequestDto = new TaskStatusRequestDto();

            taskStatusRequestDto.setSlug(dto.getSlug());

            entity.setTaskStatus(taskStatusService.convertToEntity(taskStatusRequestDto));
        }

        if (dto.getTaskLabelIds() != null) {
            entity.setLabels(labelService.convertToEntities(dto.getTaskLabelIds()));
        }

        if (dto.getAssigneeId() != null) {
            var userRequestDto = new UserRequestDto();

            userRequestDto.setId(dto.getAssigneeId());

            entity.setAssignee(userService.convertToEntity(userRequestDto));
        }
    }

    @Override
    public String getErrorMessage() {
        return "Задача с id %s не найдена";
    }

    public List<TaskResponseDto> findByFilter(FilterRequestDto filter) {
        var tasks = taskRepository
                .findByFilter(filter.getTitleCont(), filter.getAssigneeId(), filter.getSlug(), filter.getLabelId());

        return tasks.stream().map(this::convertToResponseDto).toList();
    }
}
