package hexlet.code.app.utils;

import hexlet.code.app.dtos.LabelDto;
import hexlet.code.app.models.Label;
import hexlet.code.app.repositories.LabelRepository;
import org.springframework.stereotype.Service;

@Service
public class LabelConverter implements Converter<LabelDto, Label>{
    private final LabelRepository labelRepository;

    public LabelConverter(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    public Label convertToEntity(LabelDto dto) {
        if (dto == null) {
            return null;
        }

        if (dto.getId() != null) {
            var entity = labelRepository.findById(dto.getId()).orElse(null);

            if (entity != null) {
                return entity;
            }
        }

        var label = new Label();

        label.setId(dto.getId());
        label.setName(dto.getName());

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
                    .createdAt(entity.getCreatedAt())
                    .build();
    }

    @Override
    public void updateEntity(LabelDto dto, Label entity) {
        if (dto.getName() != null)
            entity.setName(dto.getName());
    }
}
