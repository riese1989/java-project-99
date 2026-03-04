package hexlet.code.app.services;

import hexlet.code.app.dtos.BaseDto;
import hexlet.code.app.models.BaseEntity;
import hexlet.code.app.utils.Converter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class AbstractCrudService<D extends BaseDto, E extends BaseEntity> {
    protected final JpaRepository<E, Long> repository;
    protected final Converter<D, E> converter;

    protected AbstractCrudService(JpaRepository<E, Long> repository, Converter<D, E> converter) {
        this.repository = repository;
        this.converter = converter;
    }

    public D findById(Long id) {
        return converter.convertToDto(findByIdEntity(id));
    }

    public E findByIdEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(getErrorMessage().formatted(id)));
    }

    public List<D> findAll() {
        var entities = repository.findAll();

        return entities.stream().map(converter::convertToDto).toList();
    }

    public D create(D dto) {
        var entity = converter.convertToEntity(dto);

        var savedData = repository.save(entity);

        return converter.convertToDto(savedData);
    }

    public D update(D dto) {
        var id = dto.getId();
        var existingEntity = repository.findById(dto.getId()).orElseThrow(()
                -> new RuntimeException(getErrorMessage().formatted(id)));

        converter.updateEntity(dto, existingEntity);

        return converter.convertToDto(existingEntity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    abstract public String getErrorMessage();
}
