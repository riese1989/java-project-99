package hexlet.code.app.services;

import hexlet.code.app.dtos.LabelDto;
import hexlet.code.app.models.Label;
import hexlet.code.app.utils.Converter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class LabelService extends AbstractCrudService<LabelDto, Label> implements CommandLineRunner {


    protected LabelService(JpaRepository<Label, Long> repository, Converter<LabelDto, Label> labelConverter) {
        super(repository, labelConverter);
    }

    @Override
    public void run(String... args) throws Exception {

    }

    @Override
    public String getErrorMessage() {
        return "Метка с id %s не найдена";
    }
}
