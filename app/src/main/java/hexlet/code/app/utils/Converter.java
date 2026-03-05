package hexlet.code.app.utils;

import hexlet.code.app.dtos.requests.BaseRequestDto;
import hexlet.code.app.dtos.response.BaseResponseDto;
import hexlet.code.app.models.BaseEntity;

public interface Converter <Req extends BaseRequestDto, Res extends BaseResponseDto, E extends BaseEntity> {
    E convertToEntity(Req dto);
    Res convertToResponseDto(E entity);
    void updateEntity(Req dto, E entity);
}
