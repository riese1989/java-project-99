package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.LabelRequestDto;
import hexlet.code.app.dtos.response.LabelResponseDto;
import hexlet.code.app.models.Label;
import hexlet.code.app.repositories.LabelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class LabelService extends AbstractCrudService<LabelRequestDto, LabelResponseDto, Label> implements CommandLineRunner {
    protected LabelService(LabelRepository labelRepository) {
        super(labelRepository);
    }

    @Override
    public void run(String... args) throws Exception {
        createDefaultLabels("feature", "bug");
    }

    private void createDefaultLabels(String... names) {
        for (var name : names) {
            var labelDto = LabelRequestDto.builder().name(name).build();

            create(labelDto);

            log.info("Метка {} создана", name);
        }
    }

    @Override
    public Label convertToEntity(LabelRequestDto dto) {
        if (dto == null) {
            return null;
        }

        if (dto.getId() != null) {
            var entity = repository.findById(dto.getId()).orElse(null);

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

    @Override
    public String getErrorMessage() {
        return "Метка с id %s не найдена";
    }

    public Set<Label> convertToEntities(Set<Long> ids) {
        var labels = new HashSet<Label>();

        if (ids == null) {
            return labels;
        }

        for (var id : ids) {
            repository.findById(id).ifPresent(labels::add);

        }

        return labels;
    }
}
