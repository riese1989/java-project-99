package hexlet.code.services.impl;

import hexlet.code.dtos.requests.LabelRequestDto;
import hexlet.code.dtos.response.LabelResponseDto;
import hexlet.code.mappers.LabelMapper;
import hexlet.code.models.Label;
import hexlet.code.repositories.LabelRepository;
import hexlet.code.services.AbstractCrudService;
import hexlet.code.services.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class LabelServiceImpl extends AbstractCrudService<LabelRequestDto, LabelResponseDto, Label>
        implements CommandLineRunner, LabelService {
    private final LabelRepository labelRepository;

    protected LabelServiceImpl(LabelRepository labelRepository, LabelMapper labelMapper) {
        super(labelRepository, labelMapper);
        this.labelRepository = labelRepository;
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
    public String getErrorMessage() {
        return "Метка с id %s не найдена";
    }

    @Override
    public Set<Label> findEntities(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }

        return new HashSet<>(labelRepository.findAllById(ids));
    }
}
