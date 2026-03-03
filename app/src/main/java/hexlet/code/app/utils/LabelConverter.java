package hexlet.code.app.utils;

import hexlet.code.app.dtos.LabelDto;
import hexlet.code.app.models.Label;
import hexlet.code.app.services.TaskService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class LabelConverter implements Converter<LabelDto, Label>{
    private final TaskService taskService;
    private final TaskConverter taskConverter;

    public LabelConverter(TaskService taskService, TaskConverter taskConverter) {
        this.taskService = taskService;
        this.taskConverter = taskConverter;
    }

    @Override
    public Label convertToEntity(LabelDto dto) {
        if (dto == null) {
            return null;
        }

        var label = new Label();

        label.setId(dto.getId());
        label.setName(dto.getName());

        var tasks = dto.getTasks().stream()
                .map(taskDto -> taskService.findByIdEntity(taskDto.getId()))
                .collect(Collectors.toSet());

        label.setTasks(tasks);

        return label;
    }

    @Override
    public LabelDto convertToDto(Label entity) {
        if (entity == null) {
            return null;
        }

        return LabelDto.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .tasks(entity.getTasks().stream().map(taskConverter::convertToDto).collect(Collectors.toSet()))
                    .build();
    }

    @Override
    public void updateEntity(LabelDto dto, Label entity) {
        return ;
    }
}
