package hexlet.code.app.services;

import hexlet.code.app.dtos.LabelDto;
import hexlet.code.app.models.Label;
import hexlet.code.app.repositories.LabelRepository;
import hexlet.code.app.utils.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class LabelService extends AbstractCrudService<LabelDto, Label> implements CommandLineRunner {

    private final LabelRepository labelRepository;

    protected LabelService(LabelRepository labelRepository, Converter<LabelDto, Label> labelConverter) {
        super(labelRepository, labelConverter);
        this.labelRepository = labelRepository;
    }

    public void findOrCreate(Set<LabelDto> labelDtos) {
        if (labelDtos == null) {
            return;
        }

        labelDtos.forEach(l -> {
            var label = labelRepository.findLabelByName(l.getName());

            if (label.isEmpty()) {
                this.create(l);
            }
        });
    }

    @Override
    public void run(String... args) throws Exception {
        createDefaultLabels("feature", "bug");
    }

    private void createDefaultLabels(String... names) {
        for (var name : names) {
            var labelDto = LabelDto.builder().name(name).build();

            create(labelDto);

            log.info("Метка {} создана", name);
        }
    }

    @Override
    public String getErrorMessage() {
        return "Метка с id %s не найдена";
    }
}
