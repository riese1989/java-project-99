package hexlet.code.mappers;

import hexlet.code.dtos.requests.LabelRequestDto;
import hexlet.code.dtos.response.LabelResponseDto;
import hexlet.code.models.Label;
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
public interface LabelMapper extends BaseMapper<LabelRequestDto, LabelResponseDto, Label> {

    @Override
    Label toEntity(LabelRequestDto dto);

    @Override
    LabelResponseDto toResponse(Label entity);

    @Override
    void updateEntity(LabelRequestDto dto, @MappingTarget Label entity);
}

