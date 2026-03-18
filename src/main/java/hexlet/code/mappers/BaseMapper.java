package hexlet.code.mappers;

import org.mapstruct.MappingTarget;

public interface BaseMapper<Req, Res, E> {
    E toEntity(Req dto);
    Res toResponse(E entity);
    void updateEntity(Req dto, @MappingTarget E entity);
}
