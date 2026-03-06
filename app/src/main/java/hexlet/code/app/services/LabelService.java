package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.LabelRequestDto;
import hexlet.code.app.dtos.response.LabelResponseDto;
import hexlet.code.app.models.Label;
import hexlet.code.app.repositories.LabelRepository;
import hexlet.code.app.utils.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LabelService extends AbstractCrudService<LabelRequestDto, LabelResponseDto, Label> implements CommandLineRunner {

    protected LabelService(LabelRepository labelRepository, Converter<LabelRequestDto, LabelResponseDto, Label> labelConverter) {
        super(labelRepository, labelConverter);
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
}
