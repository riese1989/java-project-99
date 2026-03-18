package hexlet.code.app.mappers;

import hexlet.code.app.dtos.requests.TaskStatusRequestDto;
import hexlet.code.app.dtos.response.TaskStatusResponseDto;
import hexlet.code.app.models.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskStatusMapper extends BaseMapper<TaskStatusRequestDto, TaskStatusResponseDto, TaskStatus> {

    @Override
    TaskStatus toEntity(TaskStatusRequestDto dto);

    @Override
    TaskStatusResponseDto toResponse(TaskStatus entity);

    @Override
    void updateEntity(TaskStatusRequestDto dto, @MappingTarget TaskStatus entity);
}

