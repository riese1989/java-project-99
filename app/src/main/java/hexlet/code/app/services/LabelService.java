package hexlet.code.app.services;

import hexlet.code.app.dtos.LabelDto;
import hexlet.code.app.models.Label;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService implements CrudService<LabelDto, Label>, CommandLineRunner {
    @Override
    public LabelDto findById(Long id) {
        return null;
    }

    @Override
    public Label findByIdEntity(Long id) {
        return null;
    }

    @Override
    public List<LabelDto> findAll() {
        return List.of();
    }

    @Override
    public LabelDto create(LabelDto dto) {
        return null;
    }

    @Override
    public LabelDto update(LabelDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void run(String... args) throws Exception {

    }
}
