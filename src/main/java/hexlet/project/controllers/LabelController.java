package hexlet.project.controllers;

import hexlet.project.dtos.requests.LabelRequestDto;
import hexlet.project.dtos.response.LabelResponseDto;
import hexlet.project.services.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/labels")
public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDto getLabelById(@PathVariable final Long id) {
        return labelService.findById(id);
    }

    @GetMapping
    public ResponseEntity<List<LabelResponseDto>> getAllLabels() {
            var labelDtos = labelService.findAll();

            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(labelDtos.size()))
                    .header("Access-Control-Expose-Headers", "X-Total-Count")
                    .body(labelDtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDto createLabel(@RequestBody LabelRequestDto labelRequestDto) {
        return labelService.create(labelRequestDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDto updateLabel(@PathVariable final Long id, @RequestBody LabelRequestDto labelRequestDto) {
        return labelService.update(labelRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable final Long id) {
        labelService.delete(id);
    }
}
