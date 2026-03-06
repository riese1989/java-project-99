package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.BaseRequestDto;
import hexlet.code.app.dtos.response.BaseResponseDto;
import hexlet.code.app.models.BaseEntity;
import hexlet.code.app.utils.Converter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class AbstractCrudService<Req extends BaseRequestDto, Res extends BaseResponseDto, E extends BaseEntity> {
    protected final JpaRepository<E, Long> repository;
    protected final Converter<Req, Res, E> converter;

    protected AbstractCrudService(JpaRepository<E, Long> repository, Converter<Req, Res, E> converter) {
        this.repository = repository;
        this.converter = converter;
    }

    public Res findById(Long id) {
        return converter.convertToResponseDto(findByIdEntity(id));
    }

    public E findByIdEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(getErrorMessage().formatted(id)));
    }

    public List<Res> findAll() {
        var entities = repository.findAll();

        return entities.stream().map(converter::convertToResponseDto).toList();
    }

    @Transactional
    public Res create(Req dto) {
        var entity = converter.convertToEntity(dto);

        var savedData = repository.save(entity);

        return converter.convertToResponseDto(savedData);
    }

    public Res update(Req dto) {
        var id = dto.getId();
        var existingEntity = repository.findById(dto.getId()).orElseThrow(()
                -> new RuntimeException(getErrorMessage().formatted(id)));

        converter.updateEntity(dto, existingEntity);

        return converter.convertToResponseDto(existingEntity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    abstract public String getErrorMessage();
}
