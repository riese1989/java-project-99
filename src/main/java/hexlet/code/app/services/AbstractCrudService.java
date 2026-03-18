package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.BaseRequestDto;
import hexlet.code.app.dtos.response.BaseResponseDto;
import hexlet.code.app.mappers.BaseMapper;
import hexlet.code.app.models.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class AbstractCrudService<Req extends BaseRequestDto, Res extends BaseResponseDto, E extends BaseEntity> {
    protected final JpaRepository<E, Long> repository;
    private final BaseMapper<Req, Res, E> mapper;

    protected AbstractCrudService(JpaRepository<E, Long> repository, BaseMapper<Req, Res, E> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Res findById(Long id) {
        return convertToResponseDto(findByIdEntity(id));
    }

    public E findByIdEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(getErrorMessage().formatted(id)));
    }

    public List<Res> findAll() {
        var entities = repository.findAll();

        return entities.stream().map(this::convertToResponseDto).toList();
    }

    @Transactional
    public Res create(Req dto) {
        var entity = convertToEntity(dto);

        var savedData = repository.save(entity);

        return convertToResponseDto(savedData);
    }

    public Res update(Req dto) {
        var id = dto.getId();
        var existingEntity = repository.findById(dto.getId()).orElseThrow(()
                -> new RuntimeException(getErrorMessage().formatted(id)));

        updateEntity(dto, existingEntity);

        return convertToResponseDto(existingEntity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public E convertToEntity(Req dto) {
        return mapper.toEntity(dto);
    }

    public Res convertToResponseDto(E entity) {
        return mapper.toResponse(entity);
    }

    public void updateEntity(Req dto, E entity) {
        mapper.updateEntity(dto, entity);
    }

    abstract public String getErrorMessage();
}
