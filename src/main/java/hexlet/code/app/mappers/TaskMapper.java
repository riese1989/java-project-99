package hexlet.code.app.mappers;

import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.requests.UserRequestDto;
import hexlet.code.app.dtos.response.TaskResponseDto;
import hexlet.code.app.models.Label;
import hexlet.code.app.models.Task;
import hexlet.code.app.models.User;
import hexlet.code.app.services.LabelService;
import hexlet.code.app.services.TaskStatusService;
import hexlet.code.app.services.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class TaskMapper implements BaseMapper<TaskRequestDto, TaskResponseDto, Task> {

    @Autowired
    protected TaskStatusService taskStatusService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected LabelService labelService;

    @Override
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "slug", qualifiedByName = "slugToStatus")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "idToUser")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "idsToLabels")
    public abstract Task toEntity(TaskRequestDto dto);

    @Override
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "labelsToIds")
    public abstract TaskResponseDto toResponse(Task entity);

    @Override
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "slug", qualifiedByName = "slugToStatus")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "idToUser")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "idsToLabels")
    public abstract void updateEntity(TaskRequestDto dto, @MappingTarget Task entity);

    @Named("slugToStatus")
    protected hexlet.code.app.models.TaskStatus slugToStatus(String slug) {
        if (slug == null)
            return null;

        return taskStatusService.findBySlug(slug);
    }

    @Named("idToUser")
    protected User idToUser(Long id) {
        if (id == null)
            return null;

        var req = new UserRequestDto();

        req.setId(id);

        return userService.convertToEntity(req);
    }

    @Named("idsToLabels")
    protected Set<Label> idsToLabels(Set<Long> ids) {
        return ids == null ? null : labelService.findEntities(ids);
    }

    @Named("labelsToIds")
    protected Set<Long> labelsToIds(Set<Label> labels) {
        return labels == null ? null : labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
