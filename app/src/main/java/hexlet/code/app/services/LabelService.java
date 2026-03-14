package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.LabelRequestDto;
import hexlet.code.app.dtos.response.LabelResponseDto;
import hexlet.code.app.models.Label;

import java.util.List;
import java.util.Set;

public interface LabelService {
    LabelResponseDto findById(Long id);
    List<LabelResponseDto> findAll();
    LabelResponseDto create(LabelRequestDto requestDto);
    LabelResponseDto update(LabelRequestDto requestDto);
    void delete(Long id);
    Set<Label> findEntities(Set<Long> ids);
}
