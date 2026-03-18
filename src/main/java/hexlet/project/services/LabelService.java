package hexlet.project.services;

import hexlet.project.dtos.requests.LabelRequestDto;
import hexlet.project.dtos.response.LabelResponseDto;
import hexlet.project.models.Label;

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
