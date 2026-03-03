package hexlet.code.app.services;

import hexlet.code.app.dtos.LabelDto;
import hexlet.code.app.models.Label;
import hexlet.code.app.repositories.LabelRepository;
import hexlet.code.app.utils.Converter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class LabelService extends AbstractCrudService<LabelDto, Label> implements CommandLineRunner {

    private final LabelRepository labelRepository;

    protected LabelService(LabelRepository labelRepository, Converter<LabelDto, Label> labelConverter) {
        super(labelRepository, labelConverter);
        this.labelRepository = labelRepository;
    }

    public Set<Label> getLabelsByTaskId(Long id) {
        return labelRepository.findLabelsByTaskId(id).get();
    }

    @Override
    public void run(String... args) throws Exception {

    }

    @Override
    public String getErrorMessage() {
        return "Метка с id %s не найдена";
    }
}
