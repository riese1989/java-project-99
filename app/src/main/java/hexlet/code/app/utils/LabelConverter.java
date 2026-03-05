package hexlet.code.app.utils;

import hexlet.code.app.dtos.requests.LabelRequestDto;
import hexlet.code.app.dtos.response.LabelResponseDto;
import hexlet.code.app.models.Label;
import hexlet.code.app.repositories.LabelRepository;
import org.springframework.stereotype.Service;

@Service
public class LabelConverter implements Converter<LabelRequestDto, LabelResponseDto, Label>{
    private final LabelRepository labelRepository;

    public LabelConverter(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    public Label convertToEntity(LabelRequestDto dto) {
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
    public LabelResponseDto convertToResponseDto(Label entity) {
        if (entity == null) {
            return null;
        }

        return LabelResponseDto.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .createdAt(entity.getCreatedAt())
                    .build();
    }

    @Override
    public void updateEntity(LabelRequestDto dto, Label entity) {
        if (dto.getName() != null)
            entity.setName(dto.getName());
    }
}
